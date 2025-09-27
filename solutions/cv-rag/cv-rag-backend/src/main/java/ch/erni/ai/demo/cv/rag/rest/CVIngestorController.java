package ch.erni.ai.demo.cv.rag.rest;

import ch.erni.ai.demo.cv.model.Profile;
import ch.erni.ai.demo.cv.rag.config.VectorStoreFactory;
import ch.erni.ai.demo.cv.rag.service.LanguageModelService;
import ch.erni.ai.demo.cv.service.CVService;
import ch.erni.ai.llm.service.ModelRegistry;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/api/cv/ingest")

@RequiredArgsConstructor
public class CVIngestorController {

    private final static int DIMENSIONS = 768;

    public enum Namespace {
        PROFILE_FULL("profile_full"),
        PROFILE_SUMMARY("profile_summary"),
        PROFILE_SKILLS("profile_skills"),
        PROFILE_PROJECTS("profile_projects");

        private final String type;

        Namespace(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }


    private final ModelRegistry modelRegistry;
    private final VectorStoreFactory vectorStoreFactory;
    private final LanguageModelService languageModelService;
    private final CVService cvService;


    @NoArgsConstructor
    @AllArgsConstructor
    public static class VectorStoreIngestionResult {
        public Integer totalTokens;
        public String id;
        public String name;
        public String table;
    }

    @SneakyThrows
    @PostMapping("/profiles/vs/import/ingestor/full")
    public List<VectorStoreIngestionResult> ingestFullProfilesInVS(
    ) {
        val embeddingModel = this.modelRegistry.getCurrentEmbeddingModel();
        val tokenCountEstimator = this.modelRegistry.getTokenCountEstimatorEmbeddingModel();
        EmbeddingStoreIngestor ingestorFull = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(vectorStoreFactory.createEmbeddingStore(Namespace.PROFILE_FULL.getType(), DIMENSIONS))
                .documentSplitter(
                        DocumentSplitters.recursive(8192, 800,
                                tokenCountEstimator
                        )
                )

                .build();

        return ingestProfiles(Namespace.PROFILE_FULL.getType(), ingestorFull);
    }

    @SneakyThrows
    @PostMapping("/profiles/vs/import/ingestor/summary")
    public List<VectorStoreIngestionResult> ingestSummaryProfilesInVS(
    ) {
        val tokenCountEstimator = this.modelRegistry.getTokenCountEstimatorEmbeddingModel();
        EmbeddingStoreIngestor ingestorSummary = EmbeddingStoreIngestor.builder()
                .embeddingModel(modelRegistry.getCurrentEmbeddingModel())
                .embeddingStore(vectorStoreFactory.createEmbeddingStore(Namespace.PROFILE_SUMMARY.getType(), DIMENSIONS))
                .documentTransformer((document -> {
                    return Document.document(summarizeCV(modelRegistry.getCurrentChatModelId(), document.text()), document.metadata());
                }))
                .documentSplitter(DocumentSplitters.recursive(8192, 800,
                        tokenCountEstimator))
                .build();

        return ingestProfiles(Namespace.PROFILE_SUMMARY.getType(), ingestorSummary);
    }

    @SneakyThrows
    @PostMapping("/profiles/vs/import/ingestor/skills")
    public List<VectorStoreIngestionResult> ingestSkillsInVS(
    ) {
        val tokenCountEstimator = this.modelRegistry.getTokenCountEstimatorEmbeddingModel();
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(modelRegistry.getCurrentEmbeddingModel())
                .embeddingStore(vectorStoreFactory.createEmbeddingStore(Namespace.PROFILE_SKILLS.getType(), DIMENSIONS))
                .documentTransformer((document -> {
                    Profile p = cvService.getProfile(document.metadata().getString("id"));
                    return Document.document(p.skillsToMarkDown(), document.metadata());
                }))
                .documentSplitter(DocumentSplitters.recursive(8192, 800, tokenCountEstimator))
                .build();

        return ingestProfiles(Namespace.PROFILE_SKILLS.getType(), ingestor);
    }

    @SneakyThrows
    @PostMapping("/profiles/vs/import/projects")
    public List<VectorStoreIngestionResult> ingestProjectsInVS(
    ) {
        var tokenCountEstimator = this.modelRegistry.getTokenCountEstimatorEmbeddingModel();
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(modelRegistry.getCurrentEmbeddingModel())
                .embeddingStore(vectorStoreFactory.createEmbeddingStore(Namespace.PROFILE_PROJECTS.getType(), DIMENSIONS))
                .documentTransformer((document -> {
                    Profile p = cvService.getProfile(document.metadata().getString("id"));
                    return Document.document(p.projectsToMarkDown(), document.metadata());
                }))
                .documentSplitter(DocumentSplitters.recursive(8192, 800,
                        tokenCountEstimator))
                .build();

        return ingestProfiles(Namespace.PROFILE_PROJECTS.getType(), ingestor);
    }

    @NotNull
    private List<VectorStoreIngestionResult> ingestProfiles(String name, EmbeddingStoreIngestor ingestorSummary) {
        List<VectorStoreIngestionResult> ingestionResults = new ArrayList<>();
        final AtomicInteger current = new AtomicInteger(0);
        final AtomicInteger errors = new AtomicInteger(0);
        var profiles = cvService.getProfiles();

        //sequentially otherwise we just get timeouts on local machine
        profiles.forEach(profileShort -> {
            try {
                String md = cvService.getProfileAsMarkdown(profileShort.id);
                var map = Map.of("id", profileShort.id, "name", profileShort.name);
                Document doc = Document.document("passage:" + md, Metadata.from(map));
                var ingestionSummary = ingestorSummary.ingest(doc);
                ingestionResults.add(new VectorStoreIngestionResult(ingestionSummary.tokenUsage().totalTokenCount(), profileShort.id, profileShort.name, Namespace.PROFILE_SUMMARY.getType()));
                log.info("{}: Ingested profile {} of {}. {}: {}", name, current.incrementAndGet(), profiles.size(), profileShort.id, profileShort.name);
            } catch (Exception e) {
                errors.incrementAndGet();
                log.error("{}: Error while ingesting profile {}: {}", name, profileShort.id, profileShort.name, e);
            }
        });
        if (errors.get() > 0) {
            log.error("{}: {} profiles could not be ingested", name, errors.get());
        }
        return ingestionResults;
    }


    private String summarizeCV(String model, String cvContent) {
        return languageModelService.executeSimplePrompt(model, "cv_summary_prompt", new LanguageModelService.NameAndValue("cv_content", cvContent));
    }

    @DeleteMapping("/profiles/vs/delete/all")
    public void deleteAllProfilesInVectorStore() {
        deleteAll(Namespace.values());
    }

    @DeleteMapping("/profiles/vs/delete/{namespace}")
    public void deleteProfilesInNamespace(@PathVariable("namespace") Namespace namespace) {
        deleteAll(namespace);
    }

    private void deleteAll(Namespace... namespaces) {
        for (var ns : namespaces) {
            try {
                vectorStoreFactory.createEmbeddingStore(ns.getType(), DIMENSIONS).removeAll();
            } catch (Exception e) {
                log.error("Could not delete namespace {}", ns, e);
            }
        }
    }
}

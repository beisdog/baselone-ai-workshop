package ch.erni.ai.demo.rag.rest;

import ch.erni.ai.demo.rag.config.VectorStoreFactory;
import ch.erni.ai.demo.rag.model.cv.Profile;
import ch.erni.ai.demo.rag.service.CVService;
import ch.erni.ai.demo.rag.service.LanguageModelService;
import ch.erni.ai.demo.rag.service.ModelRegistry;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    @PostMapping("/profiles/vs/{model}/import/embeddingstore/full")
    public List<VectorStoreIngestionResult> addFullProfilesInVSThroughEmbeddingStore(
            @PathVariable String model
    ) {
        var tokenCountEstimator = this.modelRegistry.getTokenCountEstimator(model);
        var embeddingStore = this.vectorStoreFactory.createEmbeddingStore(Namespace.PROFILE_FULL.getType(), DIMENSIONS);
        List<VectorStoreIngestionResult> ingestionResults = new ArrayList<>();
        final AtomicInteger current = new AtomicInteger(0);
        final List<CVService.ProfileShort> errors = new ArrayList<>();
        var profiles = cvService.getProfiles();
        // Not parallel to see where the error happened
        profiles.forEach(profileShort -> {
            try {
                log.info("{}: Ingesting profile {} of {}. {}: {} ...", Namespace.PROFILE_FULL.getType(), current.incrementAndGet(), profiles.size(), profileShort.id, profileShort.name);
                String profileAsString = cvService.getProfileAsMarkdown(profileShort.id);
                var map = Map.of("id", profileShort.id, "name", profileShort.name);
                var splitter = DocumentSplitters.recursive(8192, 400, tokenCountEstimator);

                List<TextSegment> segments = splitter.split(Document.document(profileAsString, Metadata.from(map)));
                int tokenUsage = 0;
                val embeddingModel = modelRegistry.getEmbeddingModel(model);
                for (TextSegment segment : segments) {
                    Response<Embedding> embeddingResponse = embeddingModel.embed(segment);
                    tokenUsage = +embeddingResponse.tokenUsage().inputTokenCount();
                    embeddingStore.add(embeddingResponse.content(), segment);
                }
                ingestionResults.add(new VectorStoreIngestionResult(tokenUsage, profileShort.id, profileShort.name, Namespace.PROFILE_FULL.getType()));
                log.info("{}: Ingested profile {} of {}. {}: {} with {} segments using {} tokens", Namespace.PROFILE_FULL.getType(), current.incrementAndGet(), profiles.size(), profileShort.id, profileShort.name, segments.size(), tokenUsage);
            } catch (Exception e) {
                errors.add(profileShort);
                log.error("***** {}: Error while ingesting profile {}: {}", Namespace.PROFILE_FULL.getType(), profileShort.id, profileShort.name, e);
            }
        });
        if (!errors.isEmpty()) {
            log.error("{}: {} profiles could not be ingested: {}", Namespace.PROFILE_FULL.getType(), errors.size(), errors.stream().map(p -> p.id + "-" + p.name).collect(Collectors.joining(",")));
        }
        return ingestionResults;
    }

    @SneakyThrows
    @PostMapping("/profiles/vs/{model}/import/ingestor/full")
    public List<VectorStoreIngestionResult> ingestFullProfilesInVS(
            @PathVariable String model
    ) {
        val embeddingModel = this.modelRegistry.getEmbeddingModel(model);
        val tokenCountEstimator = this.modelRegistry.getTokenCountEstimator(model);
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
    @PostMapping("/profiles/vs/{model}/import/ingestor/summary")
    public List<VectorStoreIngestionResult> ingestSummaryProfilesInVS(
            @PathVariable String model
    ) {
        val tokenCountEstimator = this.modelRegistry.getTokenCountEstimator(model);
        EmbeddingStoreIngestor ingestorSummary = EmbeddingStoreIngestor.builder()
                .embeddingModel(modelRegistry.getEmbeddingModel(model))
                .embeddingStore(vectorStoreFactory.createEmbeddingStore(Namespace.PROFILE_SUMMARY.getType(), DIMENSIONS))
                .documentTransformer((document -> {
                    return Document.document(summarizeCV(model, document.text()), document.metadata());
                }))
                .documentSplitter(DocumentSplitters.recursive(8192, 800,
                        tokenCountEstimator))
                .build();

        return ingestProfiles(Namespace.PROFILE_SUMMARY.getType(), ingestorSummary);
    }

    @SneakyThrows
    @PostMapping("/profiles/vs/{model}/import/ingestor/skills")
    public List<VectorStoreIngestionResult> ingestSkillsInVS(
            @PathVariable String model
    ) {
        val tokenCountEstimator = this.modelRegistry.getTokenCountEstimator(model);
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(modelRegistry.getEmbeddingModel(model))
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
    @PostMapping("/profiles/vs/{model}/import/projects")
    public List<VectorStoreIngestionResult> ingestProjectsInVS(
            @PathVariable String model
    ) {
        var tokenCountEstimator = this.modelRegistry.getTokenCountEstimator(model);
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(modelRegistry.getEmbeddingModel(model))
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
        profiles.parallelStream().forEach(profileShort -> {
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

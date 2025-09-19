package ch.erni.ai.demo.rag.rest;

import ch.erni.ai.demo.rag.config.PineconeConfig;
import ch.erni.ai.demo.rag.service.CVService;
import ch.erni.ai.demo.rag.service.LanguageModelService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.model.embedding.EmbeddingModel;
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


    private final EmbeddingModel embeddingModel;
    private final PineconeConfig pineconeConfig;
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
    @PostMapping("/profiles/vs/import/embeddingstore/full")
    public List<VectorStoreIngestionResult> addFullProfilesInVSThroughEmbeddingStore() {
        // import cvs, create embeddings, store
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SneakyThrows
    @PostMapping("/profiles/vs/import/ingestor/full")
    public List<VectorStoreIngestionResult> ingestFullProfilesInVS() {
        // instantiate en EmbeddingStoreIngestor
        throw new UnsupportedOperationException("Not yet implemented");
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
                Document doc = Document.document(md, Metadata.from(map));
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

    @SneakyThrows
    @PostMapping("/profiles/vs/import/ingestor/summary")
    public List<VectorStoreIngestionResult> ingestSummaryProfilesInVS() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SneakyThrows
    @PostMapping("/profiles/vs/import/ingestor/skills")
    public List<VectorStoreIngestionResult> ingestSkillsInVS() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SneakyThrows
    @PostMapping("/profiles/vs/import/projects")
    public List<VectorStoreIngestionResult> ingestProjectsInVS() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private String summarizeCV(String cvContent) {
        return languageModelService.executeSimplePrompt("cv_summary_prompt", new LanguageModelService.NameAndValue("cv_content", cvContent));
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
                pineconeConfig.createEmbeddingStore(ns.getType()).removeAll();
            } catch (Exception e) {
                log.error("Could not delete namespace {}", ns, e);
            }
        }
    }
}

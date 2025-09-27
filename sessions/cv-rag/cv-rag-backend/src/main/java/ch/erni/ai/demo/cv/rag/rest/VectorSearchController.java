package ch.erni.ai.demo.cv.rag.rest;


import ch.erni.ai.demo.cv.rag.config.VectorStoreFactory;
import ch.erni.ai.demo.cv.rag.rest.model.SearchInput;
import ch.erni.ai.demo.cv.rag.rest.model.TextSegmentResult;
import ch.erni.ai.llm.service.ModelRegistry;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/vs")

@RequiredArgsConstructor
public class VectorSearchController {

    private final ModelRegistry modelRegistry;
    private final VectorStoreFactory vectorStoreFactory;

    @PostMapping("/search/{namespace}")
    public List<TextSegmentResult> vectorSearch(
            @PathVariable("namespace") CVIngestorController.Namespace namespace,
            @RequestBody SearchInput searchInput) {
        log.info("*** vectorSearch: {}: for query '{}' ...", namespace, searchInput.question);
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(this.vectorStoreFactory.createEmbeddingStore(namespace.getType(), 768))
                .embeddingModel(this.modelRegistry.getCurrentEmbeddingModel())
                .maxResults(searchInput.maxResults)
                //.minScore(0.75)
                .build();

        var result = contentRetriever
                .retrieve(Query.from(searchInput.question))
                .stream().map(content -> {
                    TextSegment textSegment = content.textSegment();
                    return TextSegmentResult.builder()
                            .text(textSegment.text())
                            .metadata(textSegment.metadata().toMap())
                            .namespace(namespace.getType())
                            .build();
                }).toList();
        log.info("Results:\n{}", result.stream().map(t -> "\n-------- BEGIN OF TEXTSEGMENT------\n"
                        + t.text
                        + "\n-------- END OF TEXTSEGMENT------\n")
                .collect(Collectors.joining()));
        return result;
    }

}

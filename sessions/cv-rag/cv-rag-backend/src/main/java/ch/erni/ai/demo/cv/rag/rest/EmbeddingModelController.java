package ch.erni.ai.demo.cv.rag.rest;

import ch.erni.ai.llm.service.LmStudioModelService;
import ch.erni.ai.llm.service.ModelRegistry;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/embedding")
@RequiredArgsConstructor
public class EmbeddingModelController {

    private final ModelRegistry modelRegistry;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmbeddingResponse {
        private String model;
        int dimensions;
        float[] vector;

    }

    @PostMapping()
    public EmbeddingResponse embed(@RequestBody String text) {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/count-tokens")
    public int countTokens(@RequestBody String text) {
        throw new UnsupportedOperationException();
    }
}

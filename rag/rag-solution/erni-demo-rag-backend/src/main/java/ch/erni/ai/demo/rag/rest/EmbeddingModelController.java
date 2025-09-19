package ch.erni.ai.demo.rag.rest;

import ch.erni.ai.demo.rag.service.LmStudioModelService;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/embedding")
@RequiredArgsConstructor
public class EmbeddingModelController {

    private final LmStudioModelService lmStudioModelService;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmbeddingResponse {
        private String model;
        int dimensions;
        float[] vector;

    }

    @PostMapping("/{model}")
    public EmbeddingResponse embed(@PathVariable String model, @RequestBody String text) {
        var embeddingModel = lmStudioModelService.getEmbeddingModel(model);
        var response = embeddingModel.embed(text);
        var embedding = response.content();
        return EmbeddingResponse.builder()
                .model(model)
                .dimensions(embedding.dimension())
                .vector(embedding.vector())
                .build();
    }
}

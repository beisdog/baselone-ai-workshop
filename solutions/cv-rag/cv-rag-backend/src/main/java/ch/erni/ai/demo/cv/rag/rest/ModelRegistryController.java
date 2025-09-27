package ch.erni.ai.demo.cv.rag.rest;

import ch.erni.ai.llm.model.ModelData;
import ch.erni.ai.llm.service.ModelRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/model-registry")
@RequiredArgsConstructor
public class ModelRegistryController {

    private final ModelRegistry modelRegistry;

    @GetMapping("/models")
    public List<ModelData> getModels() {
        return modelRegistry.getModels();
    }

    @PostMapping("/chat-model/{model}")
    public ModelData setChatModel(@PathVariable String model) {
        modelRegistry.setCurrentChatLanguageModel(model);
        return modelRegistry.getModels().stream().filter(m -> m.getId().equals(model)).findFirst().orElse(null);
    }
    @GetMapping("/chat-model/current")
    public ModelData getCurrentChatModel() {
        String model = modelRegistry.getCurrentChatModelId();
        return modelRegistry.getModels().stream().filter(m -> m.getId().equals(model)).findFirst().orElse(null);
    }

    @PostMapping("/embedding-model/{model}")
    public ModelData setEmbeddingModel(@PathVariable String model) {
        modelRegistry.setCurrentEmbeddingModel(model);
        return modelRegistry.getModels().stream().filter(m -> m.getId().equals(model)).findFirst().orElse(null);
    }

    @GetMapping("/embedding-model/current")
    public ModelData getCurrentEmbeddingModel() {
        String model = modelRegistry.getCurrentEmbeddingModelId();
        return modelRegistry.getModels().stream().filter(m -> m.getId().equals(model)).findFirst().orElse(null);
    }
}

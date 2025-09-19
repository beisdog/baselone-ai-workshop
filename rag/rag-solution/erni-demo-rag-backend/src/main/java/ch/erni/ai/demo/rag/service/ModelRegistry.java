package ch.erni.ai.demo.rag.service;


import ch.erni.ai.demo.rag.config.TokenizerConfig;
import ch.erni.ai.demo.rag.model.ModelData;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.HuggingFaceTokenCountEstimator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ModelRegistry {

    private final LmStudioModelService lmStudioModelService;
    private final OpenAIModelService openAIModelService;
    private final TokenizerConfig tokenizerConfig;


    public EmbeddingModel getEmbeddingModel(String id) {
        if (lmStudioModelService.hasModel(id)) {
            return lmStudioModelService.getEmbeddingModel(id);
        }
        return openAIModelService.getEmbeddingModel(id);
    }

    public ChatModel getChatLanguageModel(String id) {
        if (lmStudioModelService.hasModel(id)) {
            return lmStudioModelService.getChatLanguageModel(id);
        }
        return openAIModelService.getChatLanguageModel(id);
    }

    public List<ModelData> getModels() {
        return Stream.concat(lmStudioModelService.getModels().stream(), openAIModelService.getModels().stream())
                .toList();
    }

    public TokenCountEstimator getTokenCountEstimator(String model) {
        var tokenizer = tokenizerConfig.getTokenizer().stream().filter(t -> t.getName().equals(model)).findFirst();
        if (tokenizer.isPresent()) {
            var counter = new HuggingFaceTokenCountEstimator(tokenizer.get().getPath());
            return counter;
        } else {
            throw new IllegalArgumentException("no tokenizer for model configured. Found tokenizers: " + tokenizerConfig.getTokenizer());
        }
    }
}

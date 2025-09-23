package ch.erni.ai.demo.rag.service;


import ch.erni.ai.demo.rag.config.TokenizerConfig;
import ch.erni.ai.demo.rag.model.ModelData;
import ch.erni.ai.demo.rag.util.MyHuggingFaceTokenEstimator;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelRegistry {

    private final LmStudioModelService lmStudioModelService;
    private final TokenizerConfig tokenizerConfig;


    public EmbeddingModel getEmbeddingModel(String id) {
        if (lmStudioModelService.hasModel(id)) {
            return lmStudioModelService.getEmbeddingModel(id);
        }
        throw new IllegalArgumentException("could not find embeddingmodel with id: "+id+ "in lm studio.");
    }

    public ChatModel getChatLanguageModel(String id) {
        if (lmStudioModelService.hasModel(id)) {
            return lmStudioModelService.getChatLanguageModel(id);
        }
        throw new IllegalArgumentException("could not find chatmodel with id: "+id+ "in lm studio.");
    }

    public List<ModelData> getModels() {
        return lmStudioModelService.getModels().stream().toList();
    }

    public TokenCountEstimator getTokenCountEstimator(String model) {
        var tokenizer = tokenizerConfig.getTokenizer().stream().filter(t -> t.getName().equals(model)).findFirst();
        if (tokenizer.isPresent()) {
            return MyHuggingFaceTokenEstimator.get(tokenizer.get().getPath());
        } else {
            throw new IllegalArgumentException("no tokenizer for model configured. Found tokenizers: " + tokenizerConfig.getTokenizer());
        }
    }
}

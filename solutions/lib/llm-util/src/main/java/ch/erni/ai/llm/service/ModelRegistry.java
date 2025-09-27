package ch.erni.ai.llm.service;

import ch.erni.ai.llm.model.ModelData;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelRegistry {

    private final LmStudioModelService lmStudioModelService;
    private final TokenCountEstimatorRegistry tokenCountEstimatorRegistry;

    private ChatModel currentChatLanguageModel;
    @Getter
    @Value("${application.chat-model.model-name:openai/gpt-oss-120b}")
    private String currentChatModelId;
    private EmbeddingModel currentEmbeddingModel;
    @Getter
    @Value("${application.embedding-model.model-name:text-embedding-nomic-embed-text-v2}")
    private String currentEmbeddingModelId;
    @Getter
    private TokenCountEstimator tokenCountEstimatorEmbeddingModel;

    public ChatModel getCurrentChatLanguageModel() {
        if (this.currentChatLanguageModel != null)
            return this.currentChatLanguageModel;
        if (this.getCurrentChatModelId() != null) {
            return setCurrentChatLanguageModel(this.currentChatModelId);
        }
        throw new ModelNotFoundException("No current model set");
    }

    public EmbeddingModel getCurrentEmbeddingModel() {
        if (this.currentEmbeddingModel != null)
            return this.currentEmbeddingModel;
        if (this.currentEmbeddingModelId != null) {
            return setCurrentEmbeddingModel(this.currentEmbeddingModelId);
        }
        throw new ModelNotFoundException("No current model set");
    }

    public EmbeddingModel setCurrentEmbeddingModel(String id) {
        this.currentEmbeddingModel = getEmbeddingModel(id);
        this.currentEmbeddingModelId = id;
        this.tokenCountEstimatorEmbeddingModel = getTokenCountEstimator(id);
        return this.currentEmbeddingModel;
    }

    public ChatModel setCurrentChatLanguageModel(String id) {
        this.currentChatLanguageModel = getChatLanguageModel(id);
        this.currentChatModelId = id;
        return this.currentChatLanguageModel;
    }

    public TokenCountEstimator getTokenCountEstimator(String model) {
        return tokenCountEstimatorRegistry.getTokenCountEstimator(model);
    }

    public EmbeddingModel getEmbeddingModel(String id) {
        if (lmStudioModelService.hasModel(id)) {
            return lmStudioModelService.getEmbeddingModel(id);
        }
        throw new IllegalArgumentException("could not find embeddingmodel with id: " + id + "in lm studio.");
    }

    public ChatModel getChatLanguageModel(String id) {
        if (lmStudioModelService.hasModel(id)) {
            return lmStudioModelService.getChatLanguageModel(id);
        }
        throw new IllegalArgumentException("could not find chatmodel with id: " + id + "in lm studio.");
    }

    public List<ModelData> getModels() {
        return lmStudioModelService.getModels().stream().toList();
    }
}

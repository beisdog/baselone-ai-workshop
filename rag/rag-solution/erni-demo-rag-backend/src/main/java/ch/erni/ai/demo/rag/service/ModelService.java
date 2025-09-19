package ch.erni.ai.demo.rag.service;

import ch.erni.ai.demo.rag.model.ModelData;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;

import java.util.List;

public interface ModelService {

    List<ModelData> getModels();

    EmbeddingModel getEmbeddingModel(String id);

    ChatModel getChatLanguageModel(String id);

    default boolean hasModel(String id) {
        return getModels().stream()
                .filter(model -> model.getId().equals(id))
                .findFirst()
                .orElse(null) != null;
    }
}

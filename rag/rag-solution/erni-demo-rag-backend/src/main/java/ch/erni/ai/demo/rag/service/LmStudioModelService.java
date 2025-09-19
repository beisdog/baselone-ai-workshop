package ch.erni.ai.demo.rag.service;

import ch.erni.ai.demo.rag.model.ListModelsResponse;
import ch.erni.ai.demo.rag.model.ModelData;
import com.github.dockerjava.api.exception.NotFoundException;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class LmStudioModelService implements ModelService {

    @Cacheable("lmstudio_models")
    public List<ModelData> getModels() {
        RestTemplate restTemplate = new RestTemplate();
        var response = restTemplate.getForEntity("http://localhost:1234/v1/models", ListModelsResponse.class);
        assert response.getBody() != null;
        return response.getBody().getData().stream().peek(
                e -> e.setOwned_by("lmstudio")
        ).toList();
    }

    @Cacheable("lmstudio_embedding_models")
    public EmbeddingModel getEmbeddingModel(String id) {
        var models = this.getModels();
        var model = models.stream().
                filter(modelData -> modelData.getId().equals(id))
                .findFirst().orElse(null);
        if (model == null) {
            throw new NotFoundException("Could not find model with id " + id);
        }
        if (model.getId().contains("embed")) {
            return OpenAiEmbeddingModel
                    .builder()
                    .modelName(model.getId())
                    .baseUrl("http://localhost:1234/v1")
                    .apiKey("ignored")
                    .build();
        } else {
            throw new IllegalArgumentException("model with id " + id + " is not an embedding model");
        }
    }

    @Cacheable("lmstudio_chat_models")
    public ChatModel getChatLanguageModel(String id) {
        var models = this.getModels();
        var model = models.stream().
                filter(modelData -> modelData.getId().equals(id))
                .findFirst().orElse(null);
        if (model == null) {
            throw new NotFoundException("Could not find model with id " + id);
        }
        if (!model.getId().contains("embed")) {
            return OpenAiChatModel
                    .builder()
                    .modelName(model.getId())
                    .baseUrl("http://localhost:1234/v1")
                    .apiKey("ignored")
                    .build();
        } else {
            throw new IllegalArgumentException("model with id " + id + " is an embedding model and no chatmodel");
        }
    }
}

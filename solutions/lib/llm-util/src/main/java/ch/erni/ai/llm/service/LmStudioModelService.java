package ch.erni.ai.llm.service;

import ch.erni.ai.llm.model.ListModelsResponse;
import ch.erni.ai.llm.model.ModelData;
import dev.langchain4j.http.client.spring.restclient.SpringRestClientBuilder;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LmStudioModelService implements ModelService {

    @Value("${application.lmstudio.base-urL:http://localhost:1234/v1}")
    private String lmStudioBaseUrl;
    private final SpringRestClientBuilder springRestClientBuilder;

    @Cacheable("lmstudio_models")
    public List<ModelData> getModels() {
        RestTemplate restTemplate = new RestTemplate();
        var response = restTemplate.getForEntity(this.lmStudioBaseUrl + "/models", ListModelsResponse.class);
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
            throw new ModelNotFoundException("Could not find model with id " + id);
        }
        if (model.getId().contains("embed")) {
            return OpenAiEmbeddingModel
                    .builder()
                    .httpClientBuilder(springRestClientBuilder)
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
            throw new ModelNotFoundException("Could not find model with id " + id);
        }
        if (!model.getId().contains("embed")) {
            return OpenAiChatModel
                    .builder()
                    .modelName(model.getId())
                    .httpClientBuilder(springRestClientBuilder)
                    .baseUrl(lmStudioBaseUrl)
                    .apiKey("ignored")
                    .build();
        } else {
            throw new IllegalArgumentException("model with id " + id + " is an embedding model and no chatmodel");
        }
    }
}

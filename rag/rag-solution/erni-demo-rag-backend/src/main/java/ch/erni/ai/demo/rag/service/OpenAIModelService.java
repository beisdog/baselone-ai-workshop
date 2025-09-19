package ch.erni.ai.demo.rag.service;


import ch.erni.ai.demo.rag.model.ListModelsResponse;
import ch.erni.ai.demo.rag.model.ModelData;
import com.github.dockerjava.api.exception.NotFoundException;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OpenAIModelService implements ModelService {

    @Value("${application.llm.openai.api-key}")
    private String openAiApiKey;

    @Cacheable("openai_models")
    public List<ModelData> getModels() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAiApiKey);
        var httpEntity = new HttpEntity<>(headers);
        var response = restTemplate.exchange("https://api.openai.com/v1/models", HttpMethod.GET, httpEntity, ListModelsResponse.class);
        assert response.getBody() != null;
        return response.getBody().getData().stream()
                .filter(e -> !e.getId().contains("tts")
                        && !e.equals("gpt-4-0613") && !e.getId().contains("2025")
                )
                .peek(
                        e -> e.setOwned_by("openai")
                ).toList();
    }

    @Cacheable("openai_embedding_models")
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
                    .apiKey(this.openAiApiKey)
                    .build();
        } else {
            throw new IllegalArgumentException("model with id " + id + " is not an embedding model");
        }
    }

    @Cacheable("openai_chat_models")
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
                    .apiKey(this.openAiApiKey)
                    .build();
        } else {
            throw new IllegalArgumentException("model with id " + id + " is an embedding model and no chatmodel");
        }
    }
}

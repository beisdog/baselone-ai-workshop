package ch.erni.ai.rag;

import ch.erni.ai.basic.AbstractChat;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;

/*
 * INSTRUCTIONS FOR STUDENTS:
 * https://docs.langchain4j.dev/tutorials/rag
 *
 * Use an Embedding Model to convert string to a vector
 *
 */
public class Lesson_01EmbeddingApp {

    public static void main(String[] args) {
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel
                .builder()
                .modelName("text-embedding-nomic-embed-text-v2")
                .baseUrl("http://localhost:1234/v1")
                .httpClientBuilder(AbstractChat.getHttp1ClientBuilder())
                .build();

        // convert text to vector and print it
    }
}

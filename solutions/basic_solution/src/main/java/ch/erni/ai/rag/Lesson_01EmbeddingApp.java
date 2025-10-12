package ch.erni.ai.rag;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;

/*
 * INSTRUCTIONS FOR STUDENTS:
 * https://docs.langchain4j.dev/tutorials/rag
 *
 * 1. Use an Embedding Model to convert string to a vector
 *
 */
public class Lesson_01EmbeddingApp {

    public static void main(String[] args) {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        String text = "Hallo";
        var response = embeddingModel.embed(text);
        var vectorList = response.content().vectorAsList();
        System.out.println(vectorList);
        System.out.println("Size:" + vectorList.size());
    }
}

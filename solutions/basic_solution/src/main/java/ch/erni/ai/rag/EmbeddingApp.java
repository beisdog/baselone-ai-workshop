package ch.erni.ai.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.util.Scanner;

/*
 * INSTRUCTIONS FOR STUDENTS:
 * https://docs.langchain4j.dev/tutorials/rag
 *
 * 1. Use an Embedding Model to convert string to a vector
 * 2. Load several embeddings into the MemoryEmbeddingStore and query them
 * https://docs.langchain4j.dev/integrations/embedding-stores/in-memory
 * 3. Load some manuals from data/manuals via a documentloader and convert them to text
 * -
 *
 */
public class EmbeddingApp {

    public static void main(String[] args) {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        String text = "Hallo";
        var response = embeddingModel.embed(text);
        var vectorList = response.content().vectorAsList();
        System.out.println(vectorList);
        System.out.println("Size:" + vectorList.size());
    }
}

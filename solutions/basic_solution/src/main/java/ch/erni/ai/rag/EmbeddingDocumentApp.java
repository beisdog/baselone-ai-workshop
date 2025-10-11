package ch.erni.ai.rag;

import ch.erni.ai.llm.service.LoadingFromHuggingFaceTokenEstimator;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModelFactory;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

import java.io.File;

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
public class EmbeddingDocumentApp {

    public static void main(String[] args) {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        PgVectorEmbeddingStore pg = PgVectorEmbeddingStore.builder()
                .createTable(true)
                .table("manual_embeddings")
                .dimension(embeddingModel.dimension())
                .host("localhost")
                .port(8432)
                .user("baselone")
                .password("baselone")
                .database("baselone")
                .dropTableFirst(true)
                .build();

        File dir = new File("./data/manuals/");
        File[] manuals = dir.listFiles();
        LoadingFromHuggingFaceTokenEstimator estimator = LoadingFromHuggingFaceTokenEstimator.get("sentence-transformers/all-MiniLM-L6-v2");

        for (var manual: manuals) {
            System.out.println("Processing " + manual);
            System.out.println("--------------------------");
            Document document = FileSystemDocumentLoader.loadDocument(manual.toPath());
            var splitter = DocumentSplitters.recursive(800,400, estimator);
            var segments = splitter.split(document);
            System.out.println("Manual has: " + segments.size() + " segments");
            for (var segment: segments) {
                var response = embeddingModel.embed(segment);
                var transformed = TextSegment.textSegment(segment.text(), Metadata.metadata("file", manual.getName()));
                System.out.println(transformed);
                System.out.println("----");
                //pg.add(response.content(), transformed);
            }

        }
    }
}

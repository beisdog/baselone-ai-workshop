package ch.erni.ai.rag;

import ch.erni.ai.llm.service.LoadingFromHuggingFaceTokenEstimator;
import ch.erni.ai.util.SpinnerUtil;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;

import java.io.File;
import java.util.List;

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
public class SplittingDocumentApp {

    public static void main(String[] args) {

        File manual = new File("./data/manuals/h_4900_b.pdf");
        LoadingFromHuggingFaceTokenEstimator estimator = LoadingFromHuggingFaceTokenEstimator
                .get("sentence-transformers/all-MiniLM-L6-v2");
        //nomic-ai/nomic-embed-text-v2-moe
        //sentence-transformers/all-MiniLM-L6-v2
        var spinner = new SpinnerUtil();

        spinner.startSpinner("Loading " + manual);
        Document document = FileSystemDocumentLoader.loadDocument(manual.toPath());
        spinner.stopSpinner("Loaded.");
        System.out.println(manual.getName() + " has " + document.text().length() + " characters");
        System.out.println(manual.getName() + " has " + estimator.estimateTokenCountInText(document.text()) + " tokens");
        spinner.startSpinner("Splitting " + manual);
        var splitter = new DocumentSplitter() {

            @Override
            public List<TextSegment> split(Document document) {
                return List.of();
            }
        };
        var splitter2 = new DocumentByParagraphSplitter(800, 400, estimator);
        var splitter3 = DocumentSplitters.recursive(800, 400, estimator);

        var segments = splitter2.split(document);
        spinner.stopSpinner("Manual has: " + segments.size() + " segments");
        int i = 0;
        for (var segment : segments) {
            System.out.println("Segment: " + i + " has chars: " + segment.text().length() + " text:" + segment.text());
            System.out.println("Segment: " + i + " has tokens: " + estimator.estimateTokenCountInText(segment.text()));
            i++;
        }
    }
}

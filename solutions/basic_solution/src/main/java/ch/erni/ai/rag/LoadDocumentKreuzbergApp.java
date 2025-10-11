package ch.erni.ai.rag;

import ch.erni.ai.util.SpinnerUtil;
import com.deverni.kreuzberg.client.KreuzbergClient;
import com.deverni.kreuzberg.client.exception.KreuzbergException;
import com.deverni.kreuzberg.client.model.ExtractionConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LoadDocumentKreuzbergApp {

    public static void main(String[] args) throws IOException {
        File dir = new File("./data/manuals");
        File manual = new File(dir,"h_4900_b.pdf");

        var spinner  = new SpinnerUtil();
        spinner.startSpinner("Loading " + manual);

        var kreuzberg = new KreuzbergClient();
        try(var input = Files.newInputStream(manual.toPath())) {
            ExtractionConfig config = ExtractionConfig.builder()
                    .extractKeywords(true)
                    .keywordCount(10)
                    .deduplicateImages(true)
                    .extractEntities(true)
                    .extractImages(true)
                    .extractTables(true)
                    .autoDetectLanguage(true)
                    .build();
            var extractionResults = kreuzberg.extractStream(config, input, manual.getName());
            spinner.stopSpinner("Loaded " + manual);
            Path out = Paths.get(dir.getAbsolutePath(), manual.getName()+ ".json");
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            String json = mapper.writeValueAsString(extractionResults.getFirst());
            Files.writeString(
                    out,
                    json,
                    StandardOpenOption.CREATE
            );
            System.out.println("Wrote " + out);
        } catch (KreuzbergException e) {
            throw new RuntimeException(e);
        }
    }
}

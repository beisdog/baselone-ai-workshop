package ch.erni.ai.rag;

import ch.erni.ai.util.SpinnerUtil;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LoadDocumentApp {

    public static void main(String[] args) throws IOException {
        File dir = new File("./data/manuals");
        File manual = new File(dir,"h_4900_b.pdf");

        var spinner  = new SpinnerUtil();
        spinner.startSpinner("Loading " + manual);
        Document document = FileSystemDocumentLoader.loadDocument(manual.toPath());
        spinner.stopSpinner("Loaded " + manual);
        Path out = Paths.get(dir.getAbsolutePath(), manual.getName()+ ".txt");
        Files.writeString(
                out,
                document.text(),
                StandardOpenOption.CREATE
        );
        System.out.println("Wrote " + out);
    }
}

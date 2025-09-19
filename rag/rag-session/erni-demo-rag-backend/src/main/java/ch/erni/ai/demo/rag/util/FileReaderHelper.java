package ch.erni.ai.demo.rag.util;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FileReaderHelper {

    public static String readFileFromFileSystem(String basePath, String filePath) throws IOException {
        return Files.readString(Paths.get(basePath, filePath));
    }

    public static String readFileFromFileSystemOrClassPath(String basePath, String filePath) throws IOException, URISyntaxException {
        Path path = Paths.get(basePath, filePath);
        if (Files.exists(path)) {
            return Files.readString(path);
        } else {
            return readFileFromClasspath(filePath);
        }
    }

    public static String readFileFromClasspath(String filePath) throws IOException, URISyntaxException {
        var res = FileReaderHelper.class.getResource(filePath);
        return Files.readString(Paths.get(res.toURI()));
    }

    public static List<String> listFilesInClasspathDir(String directory) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:" + directory + "/*");

        return Arrays.stream(resources)
                .map(resource -> resource.getFilename()) // Nur der Dateiname
                .toList();
    }
}

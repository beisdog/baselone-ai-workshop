package ch.erni.ai.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileReaderHelper {

    public static String readFileFromFileSystem(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }

    public static String readFileFromFileSystemOrClassPath(String filePath) throws IOException, URISyntaxException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            return Files.readString(path);
        } else {
            return readFileFromClasspath(filePath);
        }
    }

    public static String readFileFromClasspath(String filePath) throws IOException, URISyntaxException {
        var res = FileReaderHelper.class.getResource(filePath);
        assert res != null;
        return Files.readString(Paths.get(res.toURI()));
    }

    public static List<String> listFiles(String directory) throws IOException {
        try (var stream = Files.list(Paths.get(directory))) {
            return stream
                    .filter(Files::isRegularFile)          // only files, not subdirs
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        }
    }

    public static List<String> listFilesInClasspathDir(String directory) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:" + directory + "/*");

        return Arrays.stream(resources)
                .map(Resource::getFilename) // Nur der Dateiname
                .toList();
    }
}

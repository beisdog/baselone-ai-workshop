package ch.erni.ai.demo.cv.service;


import ch.erni.ai.demo.cv.config.CVConfigProps;
import ch.erni.ai.demo.cv.model.Profile;
import ch.erni.ai.util.FileReaderHelper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CVService {

    private final CVConfigProps props;

    public static class ProfileShort {
        public String id;
        public String name;
        public CareerInfoShort careerinfo;
    }

    public static class CareerInfoShort {
        public String AdvertisingText;
        public String LongAdvertisingText;
    }

    /**
     * Factory method.
     * @return new CVService instance
     */
    public static CVService create() {
        return create("./data/cv_data");
    }

    public static CVService create(String sourceDir) {
        var props = new CVConfigProps();
        if (sourceDir == null) {
            props.setSourceDir("./data/cv_data");
        } else {
            props.setSourceDir(sourceDir);
        }
        var objectMapper = new ObjectMapper()
                .enable(
                        SerializationFeature.INDENT_OUTPUT
                )
                .disable(
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
                )
                ;
        return new CVService(props, objectMapper);
    }

    private final ObjectMapper objectMapper;
    @Getter
    private final List<ProfileShort> profiles = new ArrayList<>();

    @SneakyThrows
    @PostConstruct
    public void init() {

        List<String> files = FileReaderHelper.listFiles(props.getSourceDir());
        for (String file : files) {
            ProfileShort profile = objectMapper.readValue(FileReaderHelper.readFileFromFileSystem(props.getSourceDir() + file), ProfileShort.class);
            profile.id = file.substring(0, file.lastIndexOf("."));
            profiles.add(profile);
        }
    }

    public List<ProfileShort> getProfiles() {
        if (profiles.isEmpty()) {
            init();
        }
        return profiles;
    }

    @SneakyThrows
    public ProfileShort getProfileShort(String id) {
        return objectMapper.readValue(FileReaderHelper.readFileFromFileSystem(props.getSourceDir() + id + ".json"), ProfileShort.class);
    }

    @SneakyThrows
    public Profile getProfile(String id) {
        return objectMapper.readValue(FileReaderHelper.readFileFromFileSystem(props.getSourceDir() + id + ".json"), Profile.class);
    }

    @SneakyThrows
    public String getProfileAsMarkdown(String id) {
        return getProfile(id).toMarkDown();
    }
}

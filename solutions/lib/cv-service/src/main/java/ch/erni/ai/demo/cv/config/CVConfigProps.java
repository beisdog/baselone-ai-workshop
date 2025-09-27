package ch.erni.ai.demo.cv.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("application.cv")
@Data
public class CVConfigProps {
    private String sourceDir;

    public String getSourceDir() {
        if (sourceDir == null || sourceDir.isEmpty()) {
            return sourceDir;
        }
        return sourceDir.endsWith("/") ? sourceDir : sourceDir + "/";
    }
}

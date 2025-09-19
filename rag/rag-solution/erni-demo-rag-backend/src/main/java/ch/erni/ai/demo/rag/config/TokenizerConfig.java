package ch.erni.ai.demo.rag.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "application")
@Data
public class TokenizerConfig {

    @Data
    public static class TokenizerPath {
        private String name;
        private String path;
    }

    private List<TokenizerPath> tokenizer;
}

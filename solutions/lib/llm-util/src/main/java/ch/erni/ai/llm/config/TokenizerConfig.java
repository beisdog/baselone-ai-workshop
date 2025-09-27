package ch.erni.ai.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "application.huggingface")
@Data
public class TokenizerConfig {

    @Data
    public static class TokenizerPath {
        private String name;
        private String path;
    }

    private List<TokenizerPath> tokenizer;
}

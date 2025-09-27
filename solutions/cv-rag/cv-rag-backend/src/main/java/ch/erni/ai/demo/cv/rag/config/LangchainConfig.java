package ch.erni.ai.demo.cv.rag.config;

import dev.langchain4j.http.client.spring.restclient.SpringRestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangchainConfig {

    @Bean
    public SpringRestClientBuilder springRestClientBuilder() {
        return new SpringRestClientBuilder();
    }
}

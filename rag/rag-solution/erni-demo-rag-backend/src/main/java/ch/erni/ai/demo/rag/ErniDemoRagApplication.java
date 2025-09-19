package ch.erni.ai.demo.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {
        "ch.erni.ai.demo.rag",
        "dev.langchain4j"
})
@SpringBootApplication
@EnableCaching
public class ErniDemoRagApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErniDemoRagApplication.class, args);
    }

}

package ch.erni.ai.demo.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {
        "ch.erni.edd.demo.rag",
        "dev.langchain4j"
})
@SpringBootApplication
public class ErniDemoRagApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErniDemoRagApplication.class, args);
    }

}

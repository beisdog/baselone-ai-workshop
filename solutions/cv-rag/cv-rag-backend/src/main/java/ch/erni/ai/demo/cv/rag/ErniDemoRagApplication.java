package ch.erni.ai.demo.cv.rag;

import ch.erni.ai.demo.cv.config.CVConfigProps;
import ch.erni.ai.demo.cv.service.CVService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {
        "ch.erni.ai.demo.cv.rag",
        "ch.erni.ai.llm",
        "dev.langchain4j"
})
@SpringBootApplication
@Import({CVService.class, CVConfigProps.class})
@EnableCaching
public class ErniDemoRagApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErniDemoRagApplication.class, args);
    }

}

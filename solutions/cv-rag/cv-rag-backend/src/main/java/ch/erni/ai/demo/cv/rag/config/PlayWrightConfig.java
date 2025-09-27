package ch.erni.ai.demo.cv.rag.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "langchain4j.playwright.mcp")
@Data
public class PlayWrightConfig {

    private String command;
    private List<String> args;

    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getArgs() {
        return args;
    }
    public void setArgs(List<String> args) {
        this.args = args;
    }
}

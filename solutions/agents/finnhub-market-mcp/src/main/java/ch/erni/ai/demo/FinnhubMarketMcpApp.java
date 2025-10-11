package ch.erni.ai.demo;

import ch.erni.ai.demo.market.MarketDataService;
import ch.erni.ai.demo.tool.market.mcp.MarketDataTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FinnhubMarketMcpApp {

    public static void main(String[] args) {
        SpringApplication.run(FinnhubMarketMcpApp.class, args);
    }

    @Bean
    public ToolCallbackProvider marketTool(@Value("${application.finnhub-api-key}")
                                           String apiKey) {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(new MarketDataTool(apiKey))
                .build();
    }
}

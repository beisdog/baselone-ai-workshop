# Slide 11: Java-Integration mit Langchain4j

**Grundlegende Integration:**
```java
// LM Studio Chat Model
ChatModel model = OllamaChatModel.builder()
    .baseUrl("http://localhost:1234")
    .modelName("llama-3.1-70b-instruct")
    .temperature(0.7)
    .timeout(Duration.ofSeconds(60))
    .build();

// AI Service Definition
@AiService
public interface EnterpriseAssistant {
    @SystemMessage("""
        Sie sind ein hilfreicher KI-Assistent für Unternehmen.
        Antworten Sie präzise, professionell und auf Deutsch.
        Wenn Sie unsicher sind, sagen Sie das ehrlich.
        """)
    String help(@UserMessage String question);
}

// Service Instantiation
EnterpriseAssistant assistant = AiServices.builder(EnterpriseAssistant.class)
    .chatLanguageModel(model)
    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
    .build();
```

**Spring Boot Integration:**
```java
@Configuration
public class LangChain4jConfig {
    
    @Bean
    public ChatModel chatModel() {
        return OllamaChatModel.builder()
            .baseUrl("${llm.base-url:http://localhost:1234}")
            .modelName("${llm.model-name:llama-3.1-70b}")
            .temperature(0.7)
            .build();
    }
    
    @Bean
    public EnterpriseAssistant enterpriseAssistant(ChatModel chatModel) {
        return AiServices.builder(EnterpriseAssistant.class)
            .chatLanguageModel(chatModel)
            .build();
    }
}
```

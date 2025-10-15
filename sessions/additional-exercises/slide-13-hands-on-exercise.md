# Slide 13: Hands-on Exercise - Erstes LLM-Setup

**Übung 1: LM Studio Setup (15 Minuten)**
1. LM Studio installieren und starten
2. Llama 3.1 8B Model herunterladen
3. Local Server auf Port 1234 starten
4. API-Test mit curl durchführen

**Übung 2: Java Integration (15 Minuten)**
```java


// Implementierung
public class FirstLLMApp {
    public static void main(String[] args) {
        ChatModel model = OllamaChatModel.builder()
            .baseUrl("http://localhost:1234")
            .modelName("llama-3.1-8b-instruct")
            .build();
            
        String response = model.generate("Erkläre Dependency Injection in Java");
        System.out.println(response);
    }
}
```

**Übung 3: AI Service Pattern (15 Minuten)**
```java
@AiService
public interface JavaTutor {
    @SystemMessage("Sie sind ein Java-Experte und Tutor.")
    String explain(@UserMessage String concept);
}

// Usage
JavaTutor tutor = AiServices.builder(JavaTutor.class)
    .chatLanguageModel(model)
    .build();
    
String explanation = tutor.explain("Was sind Java Streams?");
```

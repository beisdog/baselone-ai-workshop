# Slide 18: Context Generation für LLM

**RAG Pipeline Implementation:**
```java
@AiService
public interface CompanyKnowledgeAssistant {
    @SystemMessage("""
        Sie sind ein KI-Assistent mit Zugang zur Firmen-Wissensdatenbank.
        Verwenden Sie den bereitgestellten Kontext für präzise Antworten.
        Wenn der Kontext keine relevanten Informationen enthält, sagen Sie das klar.
        Zitieren Sie Ihre Quellen wenn möglich.
        """)
    String answer(@UserMessage String question);
}

// Setup mit Custom Content Injection
DefaultContentInjector contentInjector = DefaultContentInjector.builder()
    .promptTemplate(PromptTemplate.from(
        "{{userMessage}}\n\n" +
        "Kontext-Informationen:\n" +
        "{{contents}}\n\n" +
        "Bitte antworten Sie basierend auf dem obigen Kontext."
    ))
    .metadataKeysToInclude(Arrays.asList("source", "title", "date"))
    .build();

CompanyKnowledgeAssistant assistant = AiServices.builder(CompanyKnowledgeAssistant.class)
    .chatLanguageModel(chatModel)
    .contentRetriever(retriever)
    .contentInjector(contentInjector)
    .build();
```

**Query Enhancement:**
```java
// Query Transformation für bessere Retrieval
CompressingQueryTransformer queryTransformer = 
    new CompressingQueryTransformer(chatModel);

// Hybrid Search mit Web + Internal
ContentRetriever webRetriever = WebSearchContentRetriever.builder()
    .webSearchEngine(googleSearchEngine)
    .maxResults(3)
    .build();

// Router für intelligente Retriever-Auswahl
DefaultRetrievalAugmentor hybridRetriever = DefaultRetrievalAugmentor.builder()
    .queryTransformer(queryTransformer)
    .queryRouter(new LanguageModelQueryRouter(chatModel, Arrays.asList(
        embeddingRetriever,
        webRetriever
    )))
    .build();
```

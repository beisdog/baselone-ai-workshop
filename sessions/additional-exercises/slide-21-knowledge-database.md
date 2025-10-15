# Slide 21: Internal Knowledge Database

**Architektur-Pattern für Wissensdatenbank:**
```java
@Service
public class EnterpriseKnowledgeService {
    
    private final ContentRetriever contentRetriever;
    private final KnowledgeAssistant assistant;
    
    @PostConstruct
    public void initialize() {
        // Multi-source content retrieval
        List<ContentRetriever> retrievers = Arrays.asList(
            createDocumentRetriever(),
            createWikiRetriever(),
            createPolicyRetriever()
        );
        
        // Hybrid retrieval with routing
        this.contentRetriever = new HybridContentRetriever(retrievers);
        
        this.assistant = AiServices.builder(KnowledgeAssistant.class)
            .chatLanguageModel(chatModel)
            .contentRetriever(contentRetriever)
            .chatMemory(TokenWindowChatMemory.withMaxTokens(4000))
            .build();
    }
    
    public KnowledgeSearchResult search(String query, String userId) {
        // Add user-specific filtering
        ContentRetriever userFilteredRetriever = contentRetriever
            .withDynamicFilter(createUserFilter(userId));
            
        String response = assistant.answer(query);
        List<Content> sources = contentRetriever.retrieve(Query.from(query));
        
        return new KnowledgeSearchResult(response, sources);
    }
}
```

**Enterprise Features:**
- **Multi-modal Integration**: Text, Bilder, strukturierte Daten
- **Semantic Search**: Vector embeddings für verbesserte Suche
- **Conversational Interface**: Natural language querying
- **Access Control**: User-specific content filtering

# Slide 24: Optimizing Search Results

**Retrieval Optimization Strategies:**

**1. Query Enhancement:**
```java
// Query Expansion für bessere Retrieval
@AiService
public interface QueryEnhancer {
    @SystemMessage("""
        Erweitern Sie die Benutzerabfrage um verwandte Begriffe und Synonyme.
        Behalten Sie die ursprüngliche Intention bei.
        """)
    String enhanceQuery(@UserMessage String originalQuery);
}

// Multi-Query Retrieval
public List<Content> enhancedRetrieval(String query) {
    // 1. Generate variations
    List<String> queryVariations = Arrays.asList(
        query,
        queryEnhancer.enhanceQuery(query),
        translateToSynonyms(query)
    );
    
    // 2. Retrieve für jede Variation
    Set<Content> allResults = new HashSet<>();
    for (String variation : queryVariations) {
        allResults.addAll(contentRetriever.retrieve(Query.from(variation)));
    }
    
    // 3. Re-rank und deduplicate
    return reRankResults(new ArrayList<>(allResults), query);
}
```

**2. Hybrid Search Implementation:**
```java
@Component
public class HybridSearchService {
    
    public List<Content> hybridSearch(String query) {
        // Vector search
        List<Content> vectorResults = vectorRetriever.retrieve(Query.from(query));
        
        // Keyword search  
        List<Content> keywordResults = elasticSearchRetriever.retrieve(Query.from(query));
        
        // Combine and re-rank
        return fusionRanking(vectorResults, keywordResults, query);
    }
    
    private List<Content> fusionRanking(
        List<Content> vectorResults, 
        List<Content> keywordResults, 
        String query
    ) {
        // Reciprocal Rank Fusion (RRF)
        Map<String, Double> scoredResults = new HashMap<>();
        
        // Score vector results
        for (int i = 0; i < vectorResults.size(); i++) {
            String id = vectorResults.get(i).textSegment().text();
            scoredResults.merge(id, 1.0 / (60 + i + 1), Double::sum);
        }
        
        // Score keyword results  
        for (int i = 0; i < keywordResults.size(); i++) {
            String id = keywordResults.get(i).textSegment().text();
            scoredResults.merge(id, 1.0 / (60 + i + 1), Double::sum);
        }
        
        // Return sorted by combined score
        return scoredResults.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .map(entry -> findContentById(entry.getKey()))
            .collect(toList());
    }
}
```

**3. Context Compression:**
```java
@AiService
public interface ContextCompressor {
    @SystemMessage("""
        Komprimieren Sie den folgenden Kontext auf die wesentlichen Informationen
        für die Beantwortung der Benutzeranfrage.
        """)
    String compressContext(
        @UserMessage String query,
        @V String context
    );
}

// Usage in RAG pipeline
public String generateCompressedResponse(String query) {
    List<Content> retrievedContent = contentRetriever.retrieve(Query.from(query));
    String rawContext = joinContent(retrievedContent);
    
    // Compress context to fit token limits
    String compressedContext = contextCompressor.compressContext(query, rawContext);
    
    return chatModel.generate(buildPrompt(query, compressedContext));
}
```

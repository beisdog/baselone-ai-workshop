# Slide 16: Text-Splitting und Chunking-Strategien

**Chunking-Strategien (5 Komplexitätslevel):**

**1. Fixed-Size Chunking:**
```java
DocumentSplitter fixedSplitter = DocumentSplitters.recursive(
    1000,  // maxTokens
    200,   // overlap
    new OpenAiTokenCountEstimator("gpt-4o-mini")
);
```

**2. Sentence-Based Chunking:**
```java
DocumentSplitter sentenceSplitter = new DocumentBySentenceSplitter(
    500,   // maxSegmentSize
    100    // overlap
);
```

**3. Semantic Chunking (Custom Implementation):**
```java
private List<TextSegment> semanticChunking(Document document) {
    // 1. Split in Sätze
    List<String> sentences = splitIntoSentences(document.text());
    
    // 2. Generate embeddings für jeden Satz
    List<Embedding> embeddings = sentences.stream()
        .map(embeddingModel::embed)
        .collect(toList());
    
    // 3. Gruppiere ähnliche Sätze
    return groupSimilarSentences(sentences, embeddings);
}
```

**Best Practices:**
- **Overlap**: 10-20% für Kontext-Kontinuität
- **Boundary Respect**: Teile an natürlichen Grenzen
- **Metadata Enhancement**: Füge Dokumenttitel/Zusammenfassungen hinzu
- **Domain Adaptation**: Anpassung für spezifische Dokumenttypen

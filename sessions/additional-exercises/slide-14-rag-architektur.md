# Slide 14: RAG-Architektur-Patterns

**3 RAG-Flavors in Langchain4j:**

**1. Easy RAG (Minimal Setup):**
```java

List<Document> documents = FileSystemDocumentLoader.loadDocuments("/docs");
InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
        .embeddingModel()
        .ingest(documents, embeddingStore);

Assistant assistant = AiServices.builder(Assistant.class)
    .chatModel(chatModel)
    .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
    .build();
```

**2. Naive RAG (Basic Vector Search):**
```java
ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
    .embeddingStore(embeddingStore)
    .embeddingModel(embeddingModel)
    .maxResults(5)
    .minScore(0.75)
    .build();
```

**3. Advanced RAG (Modulares Framework):**
```java
DefaultRetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
    .queryTransformer(new CompressingQueryTransformer(chatModel))
    .contentRetriever(contentRetriever)
    .contentAggregator(new ReRankingContentAggregator(scoringModel))
    .build();
```

# Slide 17: Building und Querying Vector Store

**Vector Store Setup:**
```java
// Lokales Embedding Model
EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

// Qdrant für Production
EmbeddingStore<TextSegment> embeddingStore = QdrantEmbeddingStore.builder()
    .collectionName("company_knowledge")
    .host("localhost")
    .port(6334)
    .build();

// Batch Ingestion
EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
    .embeddingStore(embeddingStore)
    .embeddingModel(embeddingModel)
    .documentSplitter(DocumentSplitters.recursive(800, 200))
    .build();

// Process Documents
List<Document> documents = loadCompanyDocuments();
ingestor.ingest(documents);
```

**Advanced Querying:**
```java
ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
    .embeddingStore(embeddingStore)
    .embeddingModel(embeddingModel)
    .maxResults(5)
    .minScore(0.7)
    // Dynamisches Metadata-Filtering
    .dynamicFilter(query -> metadataKey("department")
        .isEqualTo(getCurrentUserDepartment()))
    .build();
```

# Slide 19: Hands-on Exercise - RAG Implementation

**Übung 1: Document Ingestion (15 Minuten)**
```java
public class DocumentIngestionExercise {
    public static void main(String[] args) {
        // 1. Setup embedding model
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        
        // 2. Setup vector store (In-Memory für Übung)
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        
        // 3. Load und process documents
        List<Document> docs = FileSystemDocumentLoader.loadDocuments(
            "./sample-docs", new TextDocumentParser()
        );
        
        // 4. Create ingestor
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .documentSplitter(DocumentSplitters.recursive(500, 100))
            .build();
            
        // 5. Ingest documents
        ingestor.ingest(docs);
        
        System.out.println("Ingested " + docs.size() + " documents");
    }
}
```

**Übung 2: RAG Query (15 Minuten)**
```java
// Setup retriever
ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
    .embeddingStore(embeddingStore)
    .embeddingModel(embeddingModel)
    .maxResults(3)
    .minScore(0.6)
    .build();

// Create RAG assistant
@AiService
interface DocumentAssistant {
    String answer(@UserMessage String question);
}

DocumentAssistant assistant = AiServices.builder(DocumentAssistant.class)
    .chatLanguageModel(chatModel)
    .contentRetriever(retriever)
    .build();

// Test queries
String response1 = assistant.answer("Was steht in den Dokumenten über Sicherheit?");
String response2 = assistant.answer("Welche Richtlinien gibt es für Homeoffice?");
```

**Übung 3: Metadata Filtering (15 Minuten)**
```java
// Add metadata during ingestion
EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
    .documentTransformer(doc -> {
        String filename = doc.metadata().getString("file_name");
        if (filename.contains("hr")) {
            doc.metadata().put("category", "human-resources");
        } else if (filename.contains("tech")) {
            doc.metadata().put("category", "technical");
        }
        return doc;
    })
    // ... rest of setup
    .build();

// Filter by category during retrieval
ContentRetriever hrRetriever = EmbeddingStoreContentRetriever.builder()
    .embeddingStore(embeddingStore)
    .embeddingModel(embeddingModel)
    .filter(metadataKey("category").isEqualTo("human-resources"))
    .build();
```

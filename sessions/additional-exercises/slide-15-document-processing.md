# Slide 15: Document Processing Pipeline

**Unterstützte Dokument-Loader:**
```java
// PDF Processing
Document pdfDoc = FileSystemDocumentLoader.loadDocument(
    "/path/to/file.pdf", 
    new ApachePdfBoxDocumentParser()
);

// HTML Processing mit Cleaning
Document htmlDoc = UrlDocumentLoader.load(url, new TextDocumentParser());
HtmlTextExtractor transformer = new HtmlTextExtractor();
Document cleanDoc = transformer.transform(htmlDoc);

// Batch Loading mit Filtering
PathMatcher pathMatcher = FileSystems.getDefault()
    .getPathMatcher("glob:*.{pdf,docx,txt}");
List<Document> documents = FileSystemDocumentLoader.loadDocuments(
    "/docs", pathMatcher, new ApacheTikaDocumentParser()
);
```

**Document Enhancement:**
```java
EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
    .documentTransformer(document -> {
        // Metadata für Filtering hinzufügen
        document.metadata().put("userId", getCurrentUserId());
        document.metadata().put("department", "engineering");
        document.metadata().put("classification", "confidential");
        return document;
    })
    .documentSplitter(splitter)
    .embeddingModel(embeddingModel)
    .embeddingStore(embeddingStore)
    .build();
```

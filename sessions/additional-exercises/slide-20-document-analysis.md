# Slide 20: Document Analysis und Summarization

**Enterprise Use Cases:**
- **Legal Document Review**: Automatisierte Vertragsanalyse, Klausel-Identifikation
- **Financial Report Analysis**: Earnings call Transkription, Regulatory Filing Zusammenfassung
- **Technical Documentation**: API-Dokumentation, Code-Dokumentation Updates
- **Compliance Documentation**: Policy Review, Regulatory Change Analysis

**Implementation Example:**
```java
@AiService
public interface ContractAnalyzer {
    
    @UserMessage("""
        Analysieren Sie den folgenden Vertrag und extrahieren Sie:
        1. Kündigungsfristen
        2. Probezeit
        3. Gehalt/Vergütung
        4. Besondere Klauseln
        5. Rechtliche Risiken
        
        Vertrag: {{contract}}
        
        Antworten Sie strukturiert im JSON-Format.
        """)
    String analyzeContract(String contract);
    
    @SystemMessage("Sie sind ein Experte für deutsche Arbeitsverträge.")
    @UserMessage("Erstellen Sie eine Zusammenfassung: {{document}}")
    String summarizeDocument(String document);
}

// Usage with RAG
ContractAnalyzer analyzer = AiServices.builder(ContractAnalyzer.class)
    .chatLanguageModel(chatModel)
    .contentRetriever(legalDocumentRetriever)
    .build();
```

**Real-World Case Study:**
- **BQA (Bahrain)**: LLM-System mit Amazon Bedrock für Bildungsqualitätsbewertung
- **Ergebnis**: 70% Genauigkeit, 30% Zeitreduktion bei Report-Analyse

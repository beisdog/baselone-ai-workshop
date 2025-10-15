# Slide 12: Prompt Design für Enterprise-Anwendungen

**Prompt-Engineering-Prinzipien:**

**1. Klare Rollendefiniton:**
```java
@SystemMessage("""
    Sie sind ein Experte für deutsche Arbeitsrecht.
    Basis: Aktuelle Gesetze und Rechtsprechung (Stand 2025).
    Antworten: Präzise, strukturiert, mit Quellenangaben.
    Haftungsausschluss: Keine Rechtsberatung, nur Information.
    """)
```

**2. Few-Shot Learning:**
```java
@SystemMessage("""
    Beispiel-Aufgabe: Vertrag analysieren
    Input: "Kündigungsfrist in diesem Vertrag?"
    Output: "Kündigungsfrist: 3 Monate zum Quartalsende (§622 BGB)"
    
    Ihre Aufgabe: Ähnliche Analyse für neue Verträge
    """)
```

**3. Strukturierte Ausgaben:**
```java
public interface ContractAnalyzer {
    @UserMessage("""
        Analysieren Sie folgenden Vertragsauszug:
        {{contract}}
        
        Antworten Sie im JSON-Format:
        {
          "kuendigungsfrist": "...",
          "probezeit": "...",
          "gehalt": "...",
          "risiken": ["..."]
        }
        """)
    String analyzeContract(String contract);
}
```

**4. Sicherheits-Guardrails:**
```java
@SystemMessage("""
    WICHTIG: Verarbeiten Sie keine personenbezogenen Daten.
    Wenn PII erkannt wird, antworten Sie:
    "Ich kann keine personenbezogenen Daten verarbeiten."
    
    Erlaubt: Anonymisierte Beispiele, allgemeine Rechtsfragen
    Verboten: Namen, Adressen, Sozialversicherungsnummern
    """)
```

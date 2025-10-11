# Vorbereitung

LM Studio über ngrok bereitstellen:

ngrok http 1234
Kopiere die URL: setze sie ins application.yaml
base-url: https://ee55c5a4302f.ngrok-free.app

# Übersicht der Übungen
1. LM Studio benutzen
* LLM und Embedding Model herunterladen
* MCP Server DuckGoGo einbinden

2. Langchain4j: Basics
* Erster Aufruf LLM
* LLM mit History
* LLM mit Tool und AIServices
* LLM mit MCP
3. RAG Basics
* Einbetten in einen Prompt
* Embedding von einem Text erstellen
* Dimensions eines Models. Context Window
* Ein grosses Dokument (Manual) embedden -> Context Window
* Tokens zählen, Tokenizer von Huggingface
4. RAG Ingestion
* CVs laden und in Text umwandeln
* Dokument splitten anhand der Tokens
* Ingestion: Segments embedden und speichern in PgVector
4. RAG: Suchen
* Similarity Search
* Score
* Meta Filter

5. Agents

# baselone-ai-workshop
Opensource LLM Workshop at baselone 2025

# Setup with Codespace
In Github Project: Code -> Codespaces -> + New from main
* Im Terminal des Online VS Studio:
```bash
mvn clean install
```
* Öffne:
* `sessions/basic_session/src/main/java/ch/erni/ai/basic/AbstractChat.java`
    * Füge die Ngrok Url des LM Studio Servers ein. Diese bekommst Du vom Referent
      während des Workshops: https://c33f42125701.ngrok-free.app
* Öffne 
  * `sessions/basic_solution/src/main/java/ch/erni/ai/basic/Lesson_01BasicChat.java`
    Rechte Maustaste - Java ausführen
  * Warte bis alle Java Projekte indiziert sind und im Java Projekt Explorer angezeigt werden

# Starten von pgvector
```bash
docker compose -f docker/pgvector/docker-compose.yaml up -d
```

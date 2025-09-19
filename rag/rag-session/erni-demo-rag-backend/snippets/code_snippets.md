
# Maven dependencies
Already added to pom.xml, just for information
```xml
<!-- langchain4j -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
    <version>${langchain4j.version}</version>
</dependency>
<!-- openai -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai-spring-boot-starter</artifactId>
    <version>${langchain4j.version}</version>
</dependency>

<!-- Pinecone -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-pinecone</artifactId>
    <version>${langchain4j.version}</version>
</dependency>
```

# 1. Ask llm from Java
- ChatLanguageModelController.java
```java
@PostMapping("/ask/simple")
public Message ask(@RequestBody AskInput input) {
    String response = chatLanguageModel.chat(input.question);

    return
            Message.builder()
                    .text(response)
                    .type("assistant").build();
}
```
# 2. ask llm with history
```java
@PostMapping("/ask/messages")
public Message askWithMessages(@RequestBody Message[] input) {
    List<ChatMessage> messages = Stream.of(input).map(m -> {
        if (Objects.equals(m.getType(), "user")) {
            return dev.langchain4j.data.message.UserMessage.from(m.getText());
        } else if (Objects.equals(m.getType(), "system")) {
            return dev.langchain4j.data.message.SystemMessage.from(m.getText());
        }else if (Objects.equals(m.getType(), "assistant")) {
            return dev.langchain4j.data.message.SystemMessage.from(m.getText());
        }
       throw new IllegalArgumentException("Unknown message type: " + m.getType());
    }).toList();
    ChatResponse response = chatLanguageModel.chat(messages);

    return
            Message.builder()
                    .text(response.aiMessage().text())
                    .type("assistant").build();
}
```

# 3. Simple RAG
## 3.1 RAG: Embedd a single CV
- CVController.java

```java

@PostMapping("/ask/cv/{id}")
public ChatLanguageModelController.Message askAboutCV(@PathVariable("id") String id, @RequestBody ChatLanguageModelController.AskInput input) throws URISyntaxException, IOException {
    log.info("***************************** askAboutCV({}) *********************************", id);
    String cv = FileReaderHelper.readFileFromClasspath("/cv_files/" + id + ".md");
    String systemPrompt = FileReaderHelper.readFileFromFileSystemOrClassPath(resourcesDir, "/prompts/cv_rag_system_prompt.txt");
    String userPrompt = FileReaderHelper.readFileFromFileSystemOrClassPath(resourcesDir, "/prompts/cv_rag_user_prompt.txt");
    SystemMessage systemMessage = SystemMessage.from(systemPrompt);
    String userMessageText = userPrompt
            .replace("{{cv_content}}", cv)
            .replace("{{question}}", input.question);
    UserMessage userMessage = UserMessage.from(userMessageText);
    logPrompt(userMessageText);

    var response = chatLanguageModel.chat(systemMessage, userMessage);

    return
            ChatLanguageModelController.Message.builder()
                    .text(response.aiMessage().text())
                    .type("assistant").build();
}
```
# 4. Using RAG with vectorstore
## 4.1 Ingest Documents into Pinecone Vectorstore
### 4.1.1 switch to your namespace
Switch namespace in application.properties to your username
Otherwise you overwrite my namespace!!!!

### 4.1.2 Load full CVs, first try
-CVIngestorController#addFullProfilesInVSThroughEmbeddingStore

```java
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;

@SneakyThrows
@PostMapping("/profiles/vs/import/embeddingstore/full")
public List<VectorStoreIngestionResult> addFullProfilesInVSThroughEmbeddingStore() {
    // create connection to vector store
    var embeddingStore = this.pineconeConfig.createEmbeddingStore(Namespace.PROFILE_FULL.getType());
    List<VectorStoreIngestionResult> ingestionResults = new ArrayList<>();
    final AtomicInteger current = new AtomicInteger(0);
    final List<CVService.ProfileShort> errors = new ArrayList<>();
    // load all profiles into memory
    var profiles = cvService.getProfiles();
    // Not parallel to see where the error happened
    profiles.forEach(profileShort -> {
        try {
            log.info("{}: Ingesting profile {} of {}. {}: {} ...", Namespace.PROFILE_FULL.getType(), current.incrementAndGet(), profiles.size(), profileShort.id, profileShort.name);
            // get one profile as markdown
            String profileAsString = cvService.getProfileAsMarkdown(profileShort.id);
            // create metainformation
            var map = Map.of("id", profileShort.id, "name", profileShort.name);
            TextSegment segment = TextSegment.textSegment(profileAsString, Metadata.from(map));
            int tokenUsage = 0;
            Response<Embedding> embeddingResponse = embeddingModel.embed(segment);
            tokenUsage = embeddingResponse.tokenUsage().inputTokenCount();
            embeddingStore.add(embeddingResponse.content(), segment);

            ingestionResults.add(new VectorStoreIngestionResult(tokenUsage, profileShort.id, profileShort.name, Namespace.PROFILE_FULL.getType()));
            log.info("{}: Ingested profile {} of {}. {}: {} with {} segments using {} tokens", Namespace.PROFILE_FULL.getType(), current.incrementAndGet(), profiles.size(), profileShort.id, profileShort.name, segments.size(), tokenUsage);
        } catch (Exception e) {
            errors.add(profileShort);
            log.error("***** {}: Error while ingesting profile {}: {}", Namespace.PROFILE_FULL.getType(), profileShort.id, profileShort.name, e);
        }
    });
    if (!errors.isEmpty()) {
        log.error("{}: {} profiles could not be ingested: {}", Namespace.PROFILE_FULL.getType(), errors.size(), errors.stream().map(p -> p.id + "-" + p.name).collect(Collectors.joining(",")));
    }
    return ingestionResults;
}
```
### 4.1.3 Load full CVs, with Document Splitter
-CVIngestorController#addFullProfilesInVSThroughEmbeddingStore

```java
@SneakyThrows
@PostMapping("/profiles/vs/import/embeddingstore/full")
public List<VectorStoreIngestionResult> addFullProfilesInVSThroughEmbeddingStore() {
    //...
    profiles.forEach(profileShort -> {
        try {
            log.info("{}: Ingesting profile {} of {}. {}: {} ...", Namespace.PROFILE_FULL.getType(), current.incrementAndGet(), profiles.size(), profileShort.id, profileShort.name);
            String profileAsString = cvService.getProfileAsMarkdown(profileShort.id);
            var map = Map.of("id", profileShort.id, "name", profileShort.name);
            var splitter = DocumentSplitters.recursive(8192, 400, new OpenAiTokenizer(OpenAiEmbeddingModelName.TEXT_EMBEDDING_ADA_002));

            List<TextSegment> segments = splitter.split(Document.document(profileAsString, Metadata.from(map)));
            int tokenUsage = 0;
            for (TextSegment segment : segments) {
                Response<Embedding> embeddingResponse = embeddingModel.embed(segment);
                tokenUsage = +embeddingResponse.tokenUsage().inputTokenCount();
                embeddingStore.add(embeddingResponse.content(), segment);
            }
            ingestionResults.add(new VectorStoreIngestionResult(tokenUsage, profileShort.id, profileShort.name, Namespace.PROFILE_FULL.getType()));
            log.info("{}: Ingested profile {} of {}. {}: {} with {} segments using {} tokens", Namespace.PROFILE_FULL.getType(), current.incrementAndGet(), profiles.size(), profileShort.id, profileShort.name, segments.size(), tokenUsage);
        } catch (Exception e) {
            errors.add(profileShort);
            log.error("***** {}: Error while ingesting profile {}: {}", Namespace.PROFILE_FULL.getType(), profileShort.id, profileShort.name, e);
        }
    });
    //...
}
```

### 4.1.4 Use ingestor
```java
@SneakyThrows
@PostMapping("/profiles/vs/import/ingestor/full")
public List<VectorStoreIngestionResult> ingestFullProfilesInVS() {
    EmbeddingStoreIngestor ingestorFull = EmbeddingStoreIngestor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(pineconeConfig.createEmbeddingStore(Namespace.PROFILE_FULL.getType()))
            .documentSplitter(DocumentSplitters.recursive(8192, 800, new OpenAiTokenizer(OpenAiEmbeddingModelName.TEXT_EMBEDDING_ADA_002)))
            .build();

    return ingestProfiles(Namespace.PROFILE_FULL.getType(), ingestorFull);
}

@NotNull
private List<VectorStoreIngestionResult> ingestProfiles(String name, EmbeddingStoreIngestor ingestorSummary) {
    List<VectorStoreIngestionResult> ingestionResults = new ArrayList<>();
    final AtomicInteger current = new AtomicInteger(0);
    final AtomicInteger errors = new AtomicInteger(0);
    var profiles = cvService.getProfiles();
    profiles.parallelStream().forEach(profileShort -> {
        try {
            String md = cvService.getProfileAsMarkdown(profileShort.id);
            var map = Map.of("id", profileShort.id, "name", profileShort.name);
            Document doc = Document.document(md, Metadata.from(map));
            var ingestionSummary = ingestorSummary.ingest(doc);
            ingestionResults.add(new VectorStoreIngestionResult(ingestionSummary.tokenUsage().totalTokenCount(), profileShort.id, profileShort.name, Namespace.PROFILE_SUMMARY.getType()));
            log.info("{}: Ingested profile {} of {}. {}: {}", name, current.incrementAndGet(), profiles.size(), profileShort.id, profileShort.name);
        } catch (Exception e) {
            errors.incrementAndGet();
            log.error("{}: Error while ingesting profile {}: {}", name, profileShort.id, profileShort.name, e);
        }
    });
    if (errors.get() > 0) {
        log.error("{}: {} profiles could not be ingested", name, errors.get());
    }
    return ingestionResults;
}
```

### 4.1.5 other ingestors with document transformer
```java
@SneakyThrows
@PostMapping("/profiles/vs/import/ingestor/summary")
public List<VectorStoreIngestionResult> ingestSummaryProfilesInVS() {
    EmbeddingStoreIngestor ingestorSummary = EmbeddingStoreIngestor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(pineconeConfig.createEmbeddingStore(Namespace.PROFILE_SUMMARY.getType()))
            .documentTransformer((document -> {
                return Document.document(summarizeCV(document.text()), document.metadata());
            }))
            .documentSplitter(DocumentSplitters.recursive(8192, 800, new OpenAiTokenizer(OpenAiEmbeddingModelName.TEXT_EMBEDDING_ADA_002)))
            .build();

    return ingestProfiles(Namespace.PROFILE_SUMMARY.getType(), ingestorSummary);
}
private String summarizeCV(String cvContent) {
    return languageModelService.executeSimplePrompt("cv_summary_prompt", new LanguageModelService.NameAndValue("cv_content", cvContent));
}
```
### 4.1.6 other ingestors
```java
@SneakyThrows
@PostMapping("/profiles/vs/import/ingestor/skills")
public List<VectorStoreIngestionResult> ingestSkillsInVS() {
    EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(pineconeConfig.createEmbeddingStore(Namespace.PROFILE_SKILLS.getType()))
            .documentTransformer((document -> {
                Profile p = cvService.getProfile(document.metadata().getString("id"));
                return Document.document(p.skillsToMarkDown(), document.metadata());
            }))
            .documentSplitter(DocumentSplitters.recursive(8192, 800, new OpenAiTokenizer(OpenAiEmbeddingModelName.TEXT_EMBEDDING_ADA_002)))
            .build();

    return ingestProfiles(Namespace.PROFILE_SKILLS.getType(), ingestor);
}

@SneakyThrows
@PostMapping("/profiles/vs/import/projects")
public List<VectorStoreIngestionResult> ingestProjectsInVS() {
    EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(pineconeConfig.createEmbeddingStore(Namespace.PROFILE_PROJECTS.getType()))
            .documentTransformer((document -> {
                Profile p = cvService.getProfile(document.metadata().getString("id"));
                return Document.document(p.projectsToMarkDown(), document.metadata());
            }))
            .documentSplitter(DocumentSplitters.recursive(8192, 800, new OpenAiTokenizer(OpenAiEmbeddingModelName.TEXT_EMBEDDING_ADA_002)))
            .build();

    return ingestProfiles(Namespace.PROFILE_PROJECTS.getType(), ingestor);
}
```

## 4.2 Search with Vector Retriever
Do the vector search
CVController.java
```java
 @PostMapping("/profiles/vs/search/{namespace}")
public List<TextSegmentResult> vectorSearch(@PathVariable("namespace") Namespace namespace, @RequestBody SearchInput searchInput) {
    log.info("*** vectorSearch: {}: for query '{}' ...", namespace, searchInput.question);
    ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
            .embeddingStore(this.pineconeConfig.createEmbeddingStore(namespace.getType()))
            .embeddingModel(embeddingModel)
            .maxResults(searchInput.maxResults)
            //.minScore(0.75)
            .build();

    var result = contentRetriever
            .retrieve(Query.from(searchInput.question))
            .stream().map(content -> {
                TextSegment textSegment = content.textSegment();
                return TextSegmentResult.builder()
                        .text(textSegment.text())
                        .metadata(textSegment.metadata().toMap())
                        .namespace(namespace.getType())
                        .build();
            }).toList();
    log.info("Results:\n{}", result.stream().map(t -> "\n-------- BEGIN OF TEXTSEGMENT------\n"
                    + t.text
                    + "\n-------- END OF TEXTSEGMENT------\n")
            .collect(Collectors.joining()));
    return result;
}
```

## 4.3 Embed Search result into prompt
CVController

```java

@PostMapping("/ask/cv-list/{namespace}")
public ChatLanguageModelController.Message askAboutCVSearchResult(@PathVariable("namespace") Namespace namespace,
                                                                  @RequestBody SearchInput input) throws URISyntaxException, IOException {
    log.info("***************************** askAboutCVSearchResult({}) *********************************", namespace);
    List<TextSegmentResult> textSegments = vectorSearch(namespace, input);
    String textSegmentsAsString = convertTextSegmentsToString(textSegments);
    String systemPrompt = FileReaderHelper.readFileFromFileSystemOrClassPath(resourcesDir, "/prompts/cv_rag_vs_system_prompt.txt");
    String userPrompt = FileReaderHelper.readFileFromFileSystemOrClassPath(resourcesDir, "/prompts/cv_rag_vs_user_prompt.txt");
    SystemMessage systemMessage = SystemMessage.from(systemPrompt);
    String userMessageText = userPrompt
            .replace("{{cv_list}}", textSegmentsAsString)
            .replace("{{question}}", input.question);
    UserMessage userMessage = UserMessage.from(userMessageText);

    logPrompt(userMessageText);

    var response = chatLanguageModel.chat(systemMessage, userMessage);

    return
            ChatLanguageModelController.Message.builder()
                    .text(response.aiMessage().text())
                    .type("assistant").build();
}

@NotNull
private static String convertTextSegmentsToString(List<TextSegmentResult> textSegments) {
    return textSegments
            .stream()
            .map(textSegment ->
                    new StringBuilder("Profile ID:")
                            .append(textSegment.metadata.get("id")).append("\n")
                            .append("Name:").append(textSegment.metadata.get("name")).append("\n")
                            .append("CV:\n")
                            .append(textSegment.text).append("\n")
                            .append("---\n")
                            .toString()
            )
            .collect(Collectors.joining("\n"));
}
```

## 5 Use an Assistant
CV Controller
```java
public interface Assistant {

    @dev.langchain4j.service.SystemMessage("You are an assistant that can help with finding suitable CVs or answering questions about a CV or a list of CVs. You have several tools available to achieve those tasks.")
    ChatResponse chat(@dev.langchain4j.service.UserMessage String userMessage);
}

@PostMapping("/agent")
public ChatLanguageModelController.Message agentAssistForCVs(@RequestBody ChatLanguageModelController.AskInput input) {

    class Tools {
        @Tool("Get a complete CV by id")
        public String getProfile(String id) {
            return getProfileAsMarkdown(id);
        }

        @Tool("Search CV summaries from a vectorstore")
        public String searchCVsFromVectorStore(String query) {
            return convertTextSegmentsToString(vectorSearch(Namespace.PROFILE_SUMMARY, new SearchInput(query, 10)));
        }

        @Tool("Search CV skills from a vectorstore")
        public String searchCVsSkills(String query) {
            return convertTextSegmentsToString(vectorSearch(Namespace.PROFILE_SKILLS, new SearchInput(query, 20)));
        }
    }

    var memory = MessageWindowChatMemory.withMaxMessages(10);

    var assistant = AiServices.builder(Assistant.class)
            .chatLanguageModel(this.chatLanguageModel)
            .tools(new Tools())
            .chatMemory(memory)
            .build();

    var response = assistant.chat(input.getQuestion());
    log.info("agent: Memory: {}", memory.messages().stream().map(c -> c.type() + ":" + c.text()).collect(Collectors.joining("\n")));
    return ChatLanguageModelController.Message.builder()
            .text(response.aiMessage().text())
            .type("assistant").build();

}
```

## Using a local setup with ollama and pgvector
```xml
<!-- ollama -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-ollama</artifactId>
    <version>${langchain4j.version}</version>
</dependency>

<!-- pg vector -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-pgvector</artifactId>
    <version>${langchain4j.version}</version>
</dependency>
```



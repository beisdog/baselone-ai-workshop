package ch.erni.ai.demo.rag.rest;

import ch.erni.ai.demo.rag.config.PlayWrightConfig;
import ch.erni.ai.demo.rag.config.VectorStoreFactory;
import ch.erni.ai.demo.rag.model.cv.Profile;
import ch.erni.ai.demo.rag.rest.CVIngestorController.Namespace;
import ch.erni.ai.demo.rag.service.CVService;
import ch.erni.ai.demo.rag.service.ModelRegistry;
import ch.erni.ai.demo.rag.util.FileReaderHelper;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.service.AiServices;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/cv")

@RequiredArgsConstructor
public class CVController {

    @Value("${application.embedding-model.model-name}")
    public String defaultEmbeddingModel;

    @Value("${application.resources.dir}")
    private String resourcesDir;
    private final ModelRegistry modelRegistry;
    private final VectorStoreFactory vectorStoreFactory;
    private final ChatModel chatLanguageModel;
    private final CVService cvService;
    private final PlayWrightConfig playwrightConfig;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class TextSegmentResult {
        public String text;
        public Map<String, Object> metadata;
        public String namespace;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchInput {
        public String question;
        public int maxResults;
    }


    @GetMapping("/profiles")
    public List<CVService.ProfileShort> getProfiles() {
        return cvService.getProfiles();
    }

    @SneakyThrows
    @GetMapping("/profiles/{id}")
    public Profile getProfile(@PathVariable("id") String id) {
        return cvService.getProfile(id);
    }

    @SneakyThrows
    @GetMapping("/profiles/{id}/md")
    public String getProfileAsMarkdown(@PathVariable("id") String id) {
        return cvService.getProfileAsMarkdown(id);
    }

    @PostMapping("/profiles/vs/{model}/search/{namespace}")
    public List<TextSegmentResult> vectorSearch(
            @PathVariable("model") String model,
            @PathVariable("namespace") Namespace namespace,
            @RequestBody SearchInput searchInput) {
        log.info("*** vectorSearch: {}: for query '{}' ...", namespace, searchInput.question);
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(this.vectorStoreFactory.createEmbeddingStore(namespace.getType(), 768))
                .embeddingModel(this.modelRegistry.getEmbeddingModel(model))
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

    @PostMapping("/ask/cv/{id}")
    public ChatLanguageModelController.Message askAboutCV(@PathVariable("id") String id, @RequestBody ChatLanguageModelController.AskSimple input) throws URISyntaxException, IOException {
        log.info("***************************** askAboutCV({}) *********************************", id);
        String cv = FileReaderHelper.readFileFromClasspath("/cv_files/" + id + ".md");
        String systemPrompt = FileReaderHelper.readFileFromFileSystemOrClassPath(resourcesDir, "/prompts/cv_system_prompt.txt");
        String userPrompt = FileReaderHelper.readFileFromFileSystemOrClassPath(resourcesDir, "/prompts/cv_user_prompt.txt");
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

    private static void logPrompt(String userMessageText) {
        log.info("User Prompt: \n{}", userMessageText);
    }

    @PostMapping("/ask/cv-list/{namespace}")
    public ChatLanguageModelController.Message askAboutCVSearchResult(@PathVariable("namespace") Namespace namespace,
                                                                      @RequestBody SearchInput input) throws URISyntaxException, IOException {
        log.info("***************************** askAboutCVSearchResult({}) *********************************", namespace);
        List<TextSegmentResult> textSegments = vectorSearch(defaultEmbeddingModel, namespace, input);
        String textSegmentsAsString = convertTextSegmentsToString(textSegments);
        String systemPrompt = FileReaderHelper.readFileFromFileSystemOrClassPath(resourcesDir, "/prompts/cv_rag_simple_system_prompt.txt");
        String userPrompt = FileReaderHelper.readFileFromFileSystemOrClassPath(resourcesDir, "/prompts/cv_rag_simple_user_prompt.txt");
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

    public interface Assistant {

        @dev.langchain4j.service.SystemMessage("You are an assistant that can help with finding suitable CVs or answering questions about a CV or a list of CVs. You have several tools available to achieve those tasks." +
                "Check the vector store first, if there is no result then use playwright to find the data on the web. Once you have enough information respond to the question.")
        String chat(@dev.langchain4j.service.UserMessage String userMessage);
    }

    @PostMapping("/agent/{question}")
    public ChatLanguageModelController.Message agentAssistForCVs(@PathVariable String question) {

        class Tools {
            @Tool("Get a complete CV by id (number)")
            public String getProfile(String id) {
                if (id == null || id.isBlank()) {
                    return "Please specify an id! If you do not have an id then use the vectorsearch to find one.";
                }
                try {
                    var number = Integer.parseInt(id);
                    return CVController.this.getProfileAsMarkdown(String.valueOf(number));
                } catch (NumberFormatException e) {
                    return "The Id must be a string representing a number. Like '1234'.";
                }
            }

            @Tool("Search vector store by name")
            public String searchVectorStoreByName(String query) {
                return convertTextSegmentsToString(vectorSearch(defaultEmbeddingModel, Namespace.PROFILE_SUMMARY, new SearchInput(query, 10)));
            }

            @Tool("Search CV skills from a vectorstore")
            public String searchCVsSkills(String query) {
                return convertTextSegmentsToString(vectorSearch(defaultEmbeddingModel, Namespace.PROFILE_SKILLS, new SearchInput(query, 20)));
            }
        }

        var memory = MessageWindowChatMemory.withMaxMessages(10);

        Assistant assistant;
        if (playwrightConfig.getCommand() != null) {
            McpTransport playwrightTransport = new StdioMcpTransport.Builder()
                    .command(buildCommand(playwrightConfig))
                    .logEvents(true)
                    .build();

            McpClient playwrightClient = new DefaultMcpClient.Builder()
                    .key("PlaywrightMCP")
                    .transport(playwrightTransport)
                    .build();

            McpToolProvider playwrightToolProvider = McpToolProvider.builder()
                    .mcpClients(playwrightClient)
                    .filterToolNames("browser_click", "browser_select_option",
                            "browser_press_key", "browser_type", "browser_close",
                            "browser_handle_dialog", "browser_navigate",
                            "browser_navigate_back", "browser_fill_form",
                            "browser_wait_for", "browser_tabs")
                    .build();

            assistant = AiServices.builder(Assistant.class)
                    .chatModel(this.chatLanguageModel)
                    .tools(new Tools())                     // Your existing tools
                    .toolProvider(playwrightToolProvider)   // Playwright MCP tools
                    .chatMemory(memory)
                    .build();
        } else {
            assistant = AiServices.builder(Assistant.class)
                    .chatModel(this.chatLanguageModel)
                    .tools(new Tools())                     // Your existing tools
                    .chatMemory(memory)
                    .build();
        }

        var response = assistant.chat(question);
        log.info("agent: Memory: {}", memory.messages().stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n")));

        return ChatLanguageModelController.Message.builder()
                .text(response)
                .type("assistant")
                .build();
    }

    private List<String> buildCommand(PlayWrightConfig props) {
        List<String> cmd = new ArrayList<>();
        cmd.add(props.getCommand());
        if (props.getArgs() != null) cmd.addAll(props.getArgs());
        return cmd;
    }
}

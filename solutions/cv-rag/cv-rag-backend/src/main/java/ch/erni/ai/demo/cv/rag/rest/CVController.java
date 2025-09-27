package ch.erni.ai.demo.cv.rag.rest;

import ch.erni.ai.demo.cv.rag.config.PlayWrightConfig;
import ch.erni.ai.demo.cv.rag.config.VectorStoreFactory;
import ch.erni.ai.demo.cv.model.Profile;
import ch.erni.ai.demo.cv.rag.rest.CVIngestorController.Namespace;
import ch.erni.ai.demo.cv.rag.rest.model.AskSimple;
import ch.erni.ai.demo.cv.rag.rest.model.Message;
import ch.erni.ai.demo.cv.rag.rest.model.SearchInput;
import ch.erni.ai.demo.cv.rag.rest.model.TextSegmentResult;
import ch.erni.ai.llm.service.ModelRegistry;
import ch.erni.ai.demo.cv.service.CVService;
import ch.erni.ai.util.FileReaderHelper;
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
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/cv")

@RequiredArgsConstructor
public class CVController {

    @Value("${application.prompt-dir}")
    private String promptDir;
    private final ModelRegistry modelRegistry;
    private final VectorStoreFactory vectorStoreFactory;
    private final VectorSearchController vectorSearchController;
    private final CVService cvService;
    private final PlayWrightConfig playwrightConfig;


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

    @PostMapping("/profiles/vs/search/{namespace}")
    public List<TextSegmentResult> vectorSearch(@PathVariable("namespace") Namespace namespace, @RequestBody SearchInput searchInput) {
        return vectorSearchController.vectorSearch(namespace,searchInput);
    }

    @PostMapping("/ask/cv/{id}")
    public Message askAboutCV(@PathVariable("id") String id, @RequestBody AskSimple input) throws URISyntaxException, IOException {
        log.info("***************************** askAboutCV({}) *********************************", id);
        String cv = cvService.getProfileAsMarkdown(id);
        String systemPrompt = FileReaderHelper.readFileFromFileSystem(promptDir + "cv_system_prompt.txt");
        String userPrompt = FileReaderHelper.readFileFromFileSystem(promptDir + "cv_user_prompt.txt");
        SystemMessage systemMessage = SystemMessage.from(systemPrompt);
        String userMessageText = userPrompt
                .replace("{{cv_content}}", cv)
                .replace("{{question}}", input.getQuestion());
        UserMessage userMessage = UserMessage.from(userMessageText);
        logPrompt(userMessageText);

        var response = modelRegistry.getCurrentChatLanguageModel().chat(systemMessage, userMessage);

        return
                Message.builder()
                        .text(response.aiMessage().text())
                        .type("assistant").build();
    }

    private static void logPrompt(String userMessageText) {
        log.info("User Prompt: \n{}", userMessageText);
    }

    @PostMapping("/ask/cv-list/{namespace}")
    public Message askAboutCVSearchResult(@PathVariable("namespace") Namespace namespace,
                                          @RequestBody SearchInput input) throws URISyntaxException, IOException {
        log.info("***************************** askAboutCVSearchResult({}) *********************************", namespace);
        List<TextSegmentResult> textSegments = vectorSearch(namespace, input);
        String textSegmentsAsString = convertTextSegmentsToString(textSegments);
        String systemPrompt = FileReaderHelper.readFileFromFileSystem(promptDir + "cv_rag_simple_system_prompt.txt");
        String userPrompt = FileReaderHelper.readFileFromFileSystem(promptDir + "cv_rag_simple_user_prompt.txt");
        SystemMessage systemMessage = SystemMessage.from(systemPrompt);
        String userMessageText = userPrompt
                .replace("{{cv_list}}", textSegmentsAsString)
                .replace("{{question}}", input.question);
        UserMessage userMessage = UserMessage.from(userMessageText);

        logPrompt(userMessageText);

        var response = modelRegistry.getCurrentChatLanguageModel().chat(systemMessage, userMessage);

        return
                Message.builder()
                        .text(response.aiMessage().text())
                        .type("assistant")
                        .searchResults(textSegments)
                        .build();
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
    public Message agentAssistForCVs(@PathVariable String question) {

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
                return convertTextSegmentsToString(vectorSearch(Namespace.PROFILE_SUMMARY, new SearchInput(query, 10)));
            }

            @Tool("Search CV skills from a vectorstore")
            public String searchCVsSkills(String query) {
                return convertTextSegmentsToString(vectorSearch(Namespace.PROFILE_SKILLS, new SearchInput(query, 20)));
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
                    .chatModel(this.modelRegistry.getCurrentChatLanguageModel())
                    .tools(new Tools())                     // Your existing tools
                    .toolProvider(playwrightToolProvider)   // Playwright MCP tools
                    .chatMemory(memory)
                    .build();
        } else {
            assistant = AiServices.builder(Assistant.class)
                    .chatModel(this.modelRegistry.getCurrentChatLanguageModel())
                    .tools(new Tools())                     // Your existing tools
                    .chatMemory(memory)
                    .build();
        }

        var response = assistant.chat(question);
        log.info("agent: Memory: {}", memory.messages().stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n")));

        return Message.builder()
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

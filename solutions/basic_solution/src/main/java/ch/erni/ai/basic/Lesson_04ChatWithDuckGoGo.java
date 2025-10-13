package ch.erni.ai.basic;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.docker.DockerMcpTransport;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;

public class Lesson_04ChatWithDuckGoGo extends AbstractChat {

    private final ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);
    private final BasicAssistant assistant;

    public Lesson_04ChatWithDuckGoGo() {
        McpTransport transport = new DockerMcpTransport.Builder()
                .image("mcp/duckduckgo")
                .dockerHost("unix:///var/run/docker.sock")
                .build();

        McpClient mcpClient = new DefaultMcpClient.Builder()
                .key("MyMCPClient")
                .transport(transport)
                .build();
        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();
        this.assistant = AiServices.builder(BasicAssistant.class)
                .chatModel(createChatModel())
                .chatMemory(this.memory)
                .toolProvider(toolProvider)
                .build();
    }

    @Override
    public String chat(String userInput) {
        return assistant.chat(userInput);
    }

    public static void main(String[] args) {
        ChatStarter.start(new Lesson_04ChatWithDuckGoGo());
    }
}

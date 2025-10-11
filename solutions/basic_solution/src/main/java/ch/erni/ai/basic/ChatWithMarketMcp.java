package ch.erni.ai.basic;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;

public class ChatWithMarketMcp extends AbstractChat {

    private final ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);
    private final BasicAssistant assistant;

    public ChatWithMarketMcp() {
        McpTransport transport = new HttpMcpTransport.Builder()
                .sseUrl("http://localhost:8088/sse")
                .logRequests(true) // if you want to see the traffic in the log
                .logResponses(true)
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
}

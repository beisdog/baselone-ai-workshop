package ch.erni.ai.basic;

import ch.erni.ai.demo.tool.market.MarketDataTool;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;

public class ChatWithMarketTool extends AbstractChat{

    private final ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

    @Override
    public String chat(String userInput) {
        return AiServices.builder(BasicAssistant.class)
                .chatModel(createChatModel())
                .chatMemory(this.memory)
                .tools(new MarketDataTool("d3bm1q9r01qqg7bv6acgd3bm1q9r01qqg7bv6ad0"))
                .build().chat(userInput);
    }
}

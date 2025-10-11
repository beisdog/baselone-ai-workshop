package ch.erni.ai.basic;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;

public class ChatWithMemory extends AbstractChat{

    private final ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

    @Override
    public String chat(String userInput) {
        return AiServices.builder(BasicAssistant.class)
                .chatModel(createChatModel())
                .chatMemory(this.memory)
                .build().chat(userInput);
    }
}

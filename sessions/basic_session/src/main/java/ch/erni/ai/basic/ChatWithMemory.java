package ch.erni.ai.basic;


import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.service.AiServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Initialize the chat Memory and also correct the BasicAssistant interface.
 */
public class ChatWithMemory extends AbstractChat{

    private final List<ChatMessage> messages = new ArrayList<>();

    @Override
    public String chat(String userInput) {
        var model = createChatModel();
        messages.add(UserMessage.userMessage(userInput));
        // TODO implement chat with history
        return "Not implement yet";
    }
}

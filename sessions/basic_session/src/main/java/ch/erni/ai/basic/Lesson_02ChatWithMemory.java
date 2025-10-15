package ch.erni.ai.basic;


import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 1. Add the messages to a list and give them to the chatmodel to have a history.
 * 2. Try to change this to an AiService with ChatMemory using the BasicAssistant interface
 *
 * https://docs.langchain4j.dev/tutorials/ai-services
 * https://docs.langchain4j.dev/tutorials/chat-memory
 */
public class Lesson_02ChatWithMemory extends AbstractChat{

    private final List<ChatMessage> messages = new ArrayList<>();

    @Override
    public String chat(String userInput) {
        var model = createChatModel();
        messages.add(UserMessage.userMessage(userInput));
        // TODO implement chat with history
        return "Not implement yet";
    }
}

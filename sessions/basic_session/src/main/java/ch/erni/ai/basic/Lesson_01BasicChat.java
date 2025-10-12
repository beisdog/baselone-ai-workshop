package ch.erni.ai.basic;

import dev.langchain4j.model.chat.ChatModel;

/**
 * Implement the llm call.
 * https://docs.langchain4j.dev/tutorials/chat-and-language-models
 */
public class Lesson_01BasicChat extends AbstractChat {

    public String chat(String userInput) {
        ChatModel model = createChatModel();

        // TODO implement
        return "I do not know the answer. Please connect me to an LLM";
    }


    public static void main(String[] args) {
        ChatStarter.start(new Lesson_01BasicChat());
    }
}

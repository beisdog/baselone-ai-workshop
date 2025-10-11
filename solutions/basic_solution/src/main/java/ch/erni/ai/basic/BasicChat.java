package ch.erni.ai.basic;

public class BasicChat extends AbstractChat {

    public String chat(String userInput) {
        var model = createChatModel();
        return model.chat(userInput);
    }
}

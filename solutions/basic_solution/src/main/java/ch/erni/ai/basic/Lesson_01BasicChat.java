package ch.erni.ai.basic;

public class Lesson_01BasicChat extends AbstractChat {

    public String chat(String userInput) {
        var model = createChatModel();
        return model.chat(userInput);
    }

    public static void main(String[] args) {
        ChatStarter.start(new Lesson_01BasicChat());
    }
}

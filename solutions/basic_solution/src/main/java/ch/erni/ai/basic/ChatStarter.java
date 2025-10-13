package ch.erni.ai.basic;


public class ChatStarter {

    public static void start(AbstractChat chat) {
        try {
            chat.startChat();
        } catch (Exception e) {
            System.err.println("Failed to initialize chat application: " + e.getMessage());
            System.err.println("\nMake sure to:");
            System.err.println("1. Install and start LMStudio (if using local LLMs)");
            System.err.println("2. Pull a model: from LMStudio");
            System.err.println("3. Implement the chat method");
        }
    }


}

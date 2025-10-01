package ai.basic;

import java.util.Scanner;
/*
 * INSTRUCTIONS FOR STUDENTS:
 * Read:
 * https://docs.langchain4j.dev/tutorials/chat-and-language-models
 * 1. Basic Chat:
 * 1.1 Add the required LangChain4j dependencies to your project
 * 1.2. Choose your LLM provider and implement the chat method
 * 1.3. Configure the model parameters (API keys, model names, etc.)
 * 1.4 Run the application and test with questions like:
 *    - "Explain what is dependency injection"
 *    - "What are the SOLID principles?"
 *    - "How does garbage collection work in Java?"
 * 1.5. Provide a system message that you are Programming helper
 *
 * 5. Give the chat a memory: https://docs.langchain4j.dev/tutorials/chat-memory
 * 6. Use AIService https://docs.langchain4j.dev/tutorials/ai-services
 * 7. Give your AIService also a tool that it can write to your filesystem for some code
 * - https://docs.langchain4j.dev/tutorials/ai-services#tools-function-calling
 *
 * MAVEN DEPENDENCIES (add to pom.xml):
 *
 * <dependencies>
 *     <dependency>
 *         <groupId>dev.langchain4j</groupId>
 *         <artifactId>langchain4j</artifactId>
 *         <version>1.5.0-beta11</version>
 *     </dependency>
 *
 *
 *     <!-- For OpenAI or LMStudio -->
 *     <!-- <dependency>
 *         <groupId>dev.langchain4j</groupId>
 *         <artifactId>langchain4j-open-ai</artifactId>
 *         <version>1.5.0-beta11</version>
 *     </dependency> -->
 * </dependencies>
 */
public class BasicChatApp {

    public void startChat() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== LangChain4j Basic Chat Demo ===");
        System.out.println("Type your messages below. Type 'exit' to quit.\n");

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine().trim();

            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (userInput.isEmpty()) {
                continue;
            }

            try {
                // Using the simple chat(String)
                String aiResponse = chat(userInput);
                System.out.println("AI: " + aiResponse);
                System.out.println(); // Empty line for readability

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                System.out.println("Please check your model configuration and try again.\n");
            }
        }

        scanner.close();
    }

    public String chat(String userInput) {
        // TODO Implement
        return "I am not prepared to give you that answer";
    }

    public static void main(String[] args) {
        try {
            BasicChatApp app = new BasicChatApp();
            app.startChat();
        } catch (Exception e) {
            System.err.println("Failed to initialize chat application: " + e.getMessage());
            System.err.println("\nMake sure to:");
            System.err.println("1. Install and start LMStudio (if using local LLMs)");
            System.err.println("2. Pull a model: from LMStudio");
            System.err.println("3. Implement the chat method");
        }
    }
}

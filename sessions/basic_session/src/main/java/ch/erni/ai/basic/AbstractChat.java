package ch.erni.ai.basic;

import ch.erni.ai.util.SpinnerUtil;
import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.net.http.HttpClient;
import java.util.Scanner;

public abstract class AbstractChat {


    public void startChat() {
        SpinnerUtil spinner = new SpinnerUtil();

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== LangChain4j Chat Demo ===");
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
                spinner.startSpinner("Thinking...");
                String aiResponse = chat(userInput);
                spinner.stopSpinner("- AI finished thinking!");
                System.out.println("AI: " + aiResponse);
                System.out.println(); // Empty line for readability

            } catch (Exception e) {
                spinner.stopSpinner("Error occurred!");
                System.err.println("Error: " + e.getMessage());
                System.out.println("Please check your model configuration and try again.\n");
            }
        }
        scanner.close();
    }

    public abstract String chat(String userInput);

    public ChatModel createChatModel() {
        return new OpenAiChatModel.OpenAiChatModelBuilder()
                .baseUrl("http://localhost:1234/v1")
                .apiKey("Dummy")
                .modelName("openai/gpt-oss-120b")
                .httpClientBuilder(getHttp1ClientBuilder())
                .build();
    }

    public static JdkHttpClientBuilder getHttp1ClientBuilder() {
        HttpClient.Builder httpClientBuilder = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1);

        JdkHttpClientBuilder jdkHttpClientBuilder = JdkHttpClient.builder()
                .httpClientBuilder(httpClientBuilder);
        return jdkHttpClientBuilder;
    }
}

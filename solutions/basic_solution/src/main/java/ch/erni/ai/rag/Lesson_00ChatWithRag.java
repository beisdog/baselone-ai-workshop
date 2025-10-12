package ch.erni.ai.rag;

import ch.erni.ai.basic.AbstractChat;
import ch.erni.ai.basic.ChatStarter;
import ch.erni.ai.demo.cv.config.CVConfigProps;
import ch.erni.ai.demo.cv.model.Profile;
import ch.erni.ai.demo.cv.service.CVService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;

import java.util.Map;
import java.util.Scanner;

public class Lesson_00ChatWithRag extends AbstractChat {
    private ChatModel chatModel;
    private CVService cvService;
    private Profile profile;

    public Lesson_00ChatWithRag() {
        this.chatModel = createChatModel();
        var props = new CVConfigProps();
        props.setSourceDir("./data/cv_data");
        var objectMapper = new ObjectMapper()
                .enable(
                        SerializationFeature.INDENT_OUTPUT
                )
                .disable(
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
                )
                ;

        cvService = new CVService(props, objectMapper);

        System.out.println("Choose one profile by typing its number:");
        System.out.println("----------------------------------------");

        for(var p: cvService.getProfiles()) {
            System.out.println(p.id + ": " + p.name);
        }
        Scanner scanner = new Scanner(System.in);
        System.out.print(">");
        String id = scanner.nextLine().trim();
        this.profile = cvService.getProfile(id);
        System.out.println("Selected:" + profile.name);
    }

    public String chat(String userInput) {

        // assemble the prompt
        var template = PromptTemplate.from("""
                Beantworte die Frage zu folgendem CV
                
                Hier ist der CV:
                {{cv}}
                
                Hier die Frage: {{question}}
                """);
        Prompt prompt = template.apply(
                Map.of("cv", this.profile.toMarkDown(), "question",userInput)
        );

        String promptText = prompt.text();
        return chatModel.chat(promptText);
    }

    public static void main(String[] args) {
        ChatStarter.start(new Lesson_00ChatWithRag());
    }
}

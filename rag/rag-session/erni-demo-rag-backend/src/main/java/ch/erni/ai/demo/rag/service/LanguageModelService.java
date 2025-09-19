package ch.erni.ai.demo.rag.service;

import ch.erni.ai.demo.rag.util.FileReaderHelper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LanguageModelService {

    @AllArgsConstructor
    @NoArgsConstructor
    public static class NameAndValue {
        public String name;
        public String value;
    }

    private final ChatLanguageModel chatLanguageModel;
    @Value("${erni.resources.dir}")
    private String resourcesDir;

    @SneakyThrows
    public UserMessage createUserMessageFromTemplate(String templateName, NameAndValue... variableAndValues) {
        String userMessageText = FileReaderHelper.readFileFromFileSystemOrClassPath(resourcesDir, "/prompts/" + templateName + ".txt");

        // replace variables
        for(var variable: variableAndValues) {
            userMessageText = userMessageText.replace(variable.name, variable.value);
        };
        return UserMessage.from(userMessageText);
    }

    public String executeSimplePrompt(String promptName, NameAndValue... variableAndValues) {
        var message = createUserMessageFromTemplate(promptName, variableAndValues);
        return this.chatLanguageModel.chat(message).aiMessage().text();
    }

    public ChatResponse chat(ChatMessage... chatMessages) {
        return this.chatLanguageModel.chat(chatMessages);
    }

}

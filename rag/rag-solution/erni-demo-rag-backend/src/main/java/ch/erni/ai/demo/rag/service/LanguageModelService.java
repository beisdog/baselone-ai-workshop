package ch.erni.ai.demo.rag.service;

import ch.erni.ai.demo.rag.util.FileReaderHelper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
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

    private final ModelRegistry modelRegistry;
    @Value("${application.resources.dir}")
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

    @SneakyThrows
    public SystemMessage createSystemMessageFromTemplate(String templateName) {
        String systemPrompt = FileReaderHelper.readFileFromFileSystemOrClassPath(resourcesDir, "/prompts/" + templateName + ".txt");
        return SystemMessage.from(systemPrompt);
    }

    public String executeSimplePrompt(String model, String promptName, NameAndValue... variableAndValues) {
        val chatModel = modelRegistry.getChatLanguageModel(model);

        var message = createUserMessageFromTemplate(promptName, variableAndValues);
        return chatModel.chat(message).aiMessage().text();
    }

    public ChatResponse chat(String model, ChatMessage... chatMessages) {
        val chatModel = modelRegistry.getChatLanguageModel(model);
        return chatModel.chat(chatMessages);
    }

}

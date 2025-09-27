package ch.erni.ai.demo.cv.rag.service;

import ch.erni.ai.llm.service.ModelRegistry;
import ch.erni.ai.util.FileReaderHelper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
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
    @Value("${application.prompt-dir}")
    private String promptDir;

    @SneakyThrows
    public UserMessage createUserMessageFromTemplate(String templateName, NameAndValue... variableAndValues) {
        String userMessageText = FileReaderHelper.readFileFromFileSystem(promptDir + templateName + ".txt");

        // replace variables
        for(var variable: variableAndValues) {
            userMessageText = userMessageText.replace(variable.name, variable.value);
        };
        return UserMessage.from(userMessageText);
    }

    @SneakyThrows
    public SystemMessage createSystemMessageFromTemplate(String templateName) {
        String systemPrompt = FileReaderHelper.readFileFromFileSystem(promptDir + templateName + ".txt");
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

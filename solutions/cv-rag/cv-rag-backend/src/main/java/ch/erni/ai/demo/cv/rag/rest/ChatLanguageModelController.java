package ch.erni.ai.demo.cv.rag.rest;

import ch.erni.ai.demo.cv.rag.rest.model.AskMessages;
import ch.erni.ai.demo.cv.rag.rest.model.AskSimple;
import ch.erni.ai.demo.cv.rag.rest.model.Message;
import ch.erni.ai.llm.model.ModelData;
import ch.erni.ai.llm.service.ModelRegistry;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/llm")
@RequiredArgsConstructor
public class ChatLanguageModelController {

    private final ModelRegistry modelRegistry;

    @PostMapping("/ask/simple")
    public Message ask(
            @RequestBody AskSimple input) {

        String response = modelRegistry.getChatLanguageModel(input.getModel()).chat(input.getQuestion());

        return
                Message.builder()
                        .text(response)
                        .type("assistant").build();
    }

    @PostMapping("/ask/messages")
    public Message askWithMessages(
            @RequestBody AskMessages input) {
        List<ChatMessage> messages = input.getMessages().stream().map(m -> {
            if (Objects.equals(m.getType(), "user")) {
                return dev.langchain4j.data.message.UserMessage.from(m.getText());
            } else if (Objects.equals(m.getType(), "system")) {
                return dev.langchain4j.data.message.SystemMessage.from(m.getText());
            }else if (Objects.equals(m.getType(), "assistant")) {
                return dev.langchain4j.data.message.AiMessage.from(m.getText());
            }
           throw new IllegalArgumentException("Unknown message type: " + m.getType());
        }).toList();
        ChatResponse response = modelRegistry.getChatLanguageModel(input.getModel()).chat(messages);

        return
                Message.builder()
                        .text(response.aiMessage().text())
                        .type("assistant").build();
    }

    @GetMapping("/models")
    public List<ModelData> getModels() {
        return modelRegistry.getModels();
    }
}

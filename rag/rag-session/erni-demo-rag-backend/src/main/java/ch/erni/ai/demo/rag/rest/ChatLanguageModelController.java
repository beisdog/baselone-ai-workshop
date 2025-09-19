package ch.erni.ai.demo.rag.rest;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/llm")
@RequiredArgsConstructor
public class ChatLanguageModelController {

    private final ChatLanguageModel chatLanguageModel;

    @Data
    public static class AskInput {
        String question;
    }

    @Data
    @Builder
    public static class Message {
        String text;
        String type;
    }

    @PostMapping("/ask/simple")
    public Message ask(@RequestBody AskInput input) {
        String response = chatLanguageModel.chat(input.question);

        return
            Message.builder()
                    .text(response)
                    .type("assistant").build();
    }

    @PostMapping("/ask/messages")
    public Message askWithMessages(@RequestBody Message[] input) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

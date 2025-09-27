package ch.erni.ai.demo.cv.rag.rest.model;

import lombok.Data;

import java.util.List;

@Data
public class AskMessages {
    List<Message> messages;
    String model;
}

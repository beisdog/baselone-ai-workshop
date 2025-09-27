package ch.erni.ai.demo.cv.rag.rest.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Message {
    String text;
    String type;
    List<TextSegmentResult> searchResults;
}

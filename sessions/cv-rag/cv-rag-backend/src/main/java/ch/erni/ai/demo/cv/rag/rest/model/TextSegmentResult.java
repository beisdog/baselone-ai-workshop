package ch.erni.ai.demo.cv.rag.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TextSegmentResult {
    public String text;
    public Map<String, Object> metadata;
    public String namespace;
}

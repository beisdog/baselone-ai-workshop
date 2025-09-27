package ch.erni.ai.demo.cv.rag.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchInput {
    public String question;
    public int maxResults;
}

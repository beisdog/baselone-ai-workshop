package ch.erni.ai.demo.rag.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelData {
    private String id;
    private String object;
    private String owned_by;
}

package ch.erni.ai.demo.rag.model;

import lombok.ToString;

@ToString
public class PreferredFunction {
    public int CodeListValueId;
    public int CodeListValueCaptionId;
    public int CodeListId;
    public String FunctionName;

    public String toMarkDown() {
        StringBuilder sb = new StringBuilder();
        sb.append(FunctionName != null ? FunctionName : "").append("\n");
        ;
        return sb.toString();
    }
}

package ch.erni.ai.demo.rag.model;

import lombok.ToString;

@ToString
public class Skill {
    public String name;

    public String toMarkDown() {
        StringBuilder sb = new StringBuilder();
        sb.append("- ").append(name != null ? name : "").append("\n");
        return sb.toString();
    }
}

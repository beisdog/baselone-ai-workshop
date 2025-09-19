package ch.erni.ai.demo.rag.model.cv;

import lombok.ToString;

@ToString
public class Education {
    public String Title;
    public String InstitutionTitle;
    public String Start;
    public String End;

    public String toMarkDown() {
        StringBuilder sb = new StringBuilder();
        sb.append("- Education:\n");
        sb.append("  - Title: ").append(Title != null ? Title : "").append("\n");
        sb.append("  - InstitutionTitle: ").append(InstitutionTitle != null ? InstitutionTitle : "").append("\n");
        sb.append("  - Start: ").append(Start != null ? Start : "").append("\n");
        sb.append("  - End: ").append(End != null ? End : "").append("\n\n");
        return sb.toString();
    }
}

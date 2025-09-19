package ch.erni.ai.demo.rag.model.cv;

import lombok.ToString;

import java.util.List;

@ToString
public class Project {
    public String Name;
    public String Start;
    public String End;
    public String Description;
    public String Role;
    public String Industry;
    public List<String> Tasks;
    public List<String> Methods;
    public List<String> Technologies;

    public String toMarkDown() {
        StringBuilder sb = new StringBuilder();
        sb.append("- Project:\n");
        sb.append("  - Name: ").append(Name != null ? Name : "").append("\n");
        sb.append("  - Start: ").append(Start != null ? Start : "").append("\n");
        sb.append("  - End: ").append(End != null ? End : "").append("\n");
        sb.append("  - Description: ").append(Description != null ? Description : "").append("\n");
        sb.append("  - Role: ").append(Role != null ? Role : "").append("\n");
        sb.append("  - Industry: ").append(Industry != null ? Industry : "").append("\n");

        if (Tasks != null && !Tasks.isEmpty()) {
            sb.append("  - Tasks:\n");
            for (String task : Tasks) {
                sb.append("    - ").append(task).append("\n");
            }
        }

        if (Methods != null && !Methods.isEmpty()) {
            sb.append("  - Methods:\n");
            for (String method : Methods) {
                sb.append("    - ").append(method).append("\n");
            }
        }

        if (Technologies != null && !Technologies.isEmpty()) {
            sb.append("  - Technologies:\n");
            for (String tech : Technologies) {
                sb.append("    - ").append(tech).append("\n");
            }
        }

        sb.append("\n");
        return sb.toString();
    }
}

package ch.erni.ai.demo.rag.model;

import java.util.List;
import lombok.*;

@ToString
public class Profile {
    public String name;
    public CareerInfo careerinfo;
    public List<Skill> skills;
    public List<Education> education;
    public List<Certificate> certificates;
    public List<Project> projects;

    public String toMarkDown() {
        StringBuilder sb = new StringBuilder();

        sb.append("## Profile\n");
        sb.append("- name: ").append(name != null ? name : "").append("\n\n");

        if (careerinfo != null) {
            sb.append("### CareerInfo\n");
            sb.append(careerinfo.toMarkDown()).append("\n");
        }

        sb.append(skillsToMarkDown());

        sb.append(educationToMarkDown());

        sb.append(certificatesToMarkDown());

        sb.append(projectsToMarkDown());

        return sb.toString();
    }

    public String skillsToMarkDown() {
        StringBuilder sb = new StringBuilder();
        if (skills != null && !skills.isEmpty()) {
            sb.append("### Skills\n");
            for (Skill skill : skills) {
                sb.append(skill.toMarkDown());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String educationToMarkDown() {
        StringBuilder sb = new StringBuilder();
        if (education != null && !education.isEmpty()) {
            sb.append("### Education\n");
            for (Education edu : education) {
                sb.append(edu.toMarkDown());
            }
        }
        return sb.toString();
    }

    public String certificatesToMarkDown() {
        StringBuilder sb = new StringBuilder();
        if (certificates != null && !certificates.isEmpty()) {
            sb.append("### Certificates\n");
            for (Certificate cert : certificates) {
                sb.append(cert.toMarkDown());
            }
        }
        return sb.toString();
    }

    public String projectsToMarkDown() {
        StringBuilder sb = new StringBuilder();
        if (projects != null && !projects.isEmpty()) {
            sb.append("### Projects\n");
            for (Project project : projects) {
                sb.append(project.toMarkDown());
            }
        }
        return sb.toString();
    }
}


package ch.erni.ai.demo.cv.model;

import lombok.ToString;

import java.util.List;

@ToString
public class CareerInfo {
    public String AdvertisingText;
    public String LongAdvertisingText;
    public String PreferredFunctions;
    public List<PreferredFunction> PreferredFunctionsList;
    public String PreferredSkills;
    public String CareerSummaryLabel;
    public String HighlightsTextLabel;
    public String AdvertisingTextLabel;
    public String LongAdvertisingTextLabel;
    public String PreferredFunctionsLabel;
    public String PreferredSkillsLabel;

    public String toMarkDown() {
        StringBuilder sb = new StringBuilder();
        sb.append("- AdvertisingText:\n").append(AdvertisingText != null ? AdvertisingText : "").append("\n");
        sb.append("- LongAdvertisingText:\n").append(LongAdvertisingText != null ? LongAdvertisingText : "").append("\n");

        return sb.toString();
    }
}

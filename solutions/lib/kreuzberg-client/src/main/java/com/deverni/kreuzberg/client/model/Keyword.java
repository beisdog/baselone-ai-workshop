package com.deverni.kreuzberg.client.model;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Represents a keyword with its score.
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class Keyword {
    
    private String term;
    private Double score;

    public Keyword() {
    }

    public Keyword(String term, Double score) {
        this.term = term;
        this.score = score;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return String.format("%s (%.4f)", term, score);
    }
}

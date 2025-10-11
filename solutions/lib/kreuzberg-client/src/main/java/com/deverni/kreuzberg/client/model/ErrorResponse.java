package com.deverni.kreuzberg.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an error response from the Kreuzberg API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("details")
    private String details;

    public ErrorResponse() {
    }

    public ErrorResponse(String message, String details) {
        this.message = message;
        this.details = details;
    }

    // Getters and Setters
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "message='" + message + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}

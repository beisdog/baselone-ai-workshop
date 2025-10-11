package com.deverni.kreuzberg.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a health check response from the Kreuzberg API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthResponse {
    
    @JsonProperty("status")
    private String status;

    public HealthResponse() {
    }

    public HealthResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isHealthy() {
        return "ok".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "HealthResponse{" +
                "status='" + status + '\'' +
                '}';
    }
}

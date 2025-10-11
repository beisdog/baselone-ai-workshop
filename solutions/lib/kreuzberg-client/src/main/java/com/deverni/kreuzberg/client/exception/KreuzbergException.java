package com.deverni.kreuzberg.client.exception;

import com.deverni.kreuzberg.client.model.ErrorResponse;

/**
 * Exception thrown when Kreuzberg API operations fail.
 */
public class KreuzbergException extends Exception {
    
    private final int statusCode;
    private final ErrorResponse errorResponse;

    public KreuzbergException(String message) {
        super(message);
        this.statusCode = 0;
        this.errorResponse = null;
    }

    public KreuzbergException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.errorResponse = null;
    }

    public KreuzbergException(int statusCode, ErrorResponse errorResponse) {
        super(String.format("Kreuzberg API error (status %d): %s", 
                statusCode, 
                errorResponse != null ? errorResponse.getMessage() : "Unknown error"));
        this.statusCode = statusCode;
        this.errorResponse = errorResponse;
    }

    public KreuzbergException(int statusCode, String message) {
        super(String.format("Kreuzberg API error (status %d): %s", statusCode, message));
        this.statusCode = statusCode;
        this.errorResponse = new ErrorResponse(message, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public boolean isClientError() {
        return statusCode >= 400 && statusCode < 500;
    }

    public boolean isServerError() {
        return statusCode >= 500;
    }
}

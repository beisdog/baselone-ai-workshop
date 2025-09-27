package ch.erni.ai.demo.cv.rag.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorResponse {
        String type;
        String message;
        LocalDateTime timestamp = LocalDateTime.now();
        StackTraceElement[] stackTrace;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Error in Rest Controller", e);
        return new ResponseEntity<>(
                new ErrorResponse(
                        e.getClass().getName(),
                        e.getMessage(),
                        LocalDateTime.now(),
                        e.getStackTrace()
                ), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}

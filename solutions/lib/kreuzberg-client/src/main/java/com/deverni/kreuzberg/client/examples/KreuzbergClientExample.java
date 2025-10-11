package com.deverni.kreuzberg.client.examples;

import com.deverni.kreuzberg.client.KreuzbergClient;
import com.deverni.kreuzberg.client.exception.KreuzbergException;
import com.deverni.kreuzberg.client.model.ExtractionConfig;
import com.deverni.kreuzberg.client.model.ExtractionResult;
import com.deverni.kreuzberg.client.model.HealthResponse;
import com.deverni.kreuzberg.client.model.Keyword;

import java.io.File;
import java.util.List;

/**
 * Example usage of the Kreuzberg REST client.
 * 
 * <p>Before running this example, ensure you have a Kreuzberg API server running:
 * <pre>
 * docker run -p 8000:8000 goldziher/kreuzberg
 * </pre>
 */
public class KreuzbergClientExample {
    
    private static final String API_URL = "http://localhost:8000";
    
    public static void main(String[] args) {

        String filePath = "./data/manuals/w1_white_edition.pdf"; //args[0];
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.err.println("Error: File not found: " + filePath);
            return;
        }
        
        // Create and use the client
        try (KreuzbergClient client = new KreuzbergClient(API_URL)) {
            
            // 1. Health check
//            System.out.println("=== Health Check ===");
//            performHealthCheck(client);
//            System.out.println();
            
            // 2. Simple extraction
//            System.out.println("=== Simple Extraction ===");
//            performSimpleExtraction(client, file);
//            System.out.println();
            
            // 3. Extraction with configuration
            System.out.println("=== Extraction with Keywords and Entities ===");
            performConfiguredExtraction(client, file);
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Performs a health check on the API.
     */
    private static void performHealthCheck(KreuzbergClient client) throws KreuzbergException {
        HealthResponse health = client.healthCheck();
        System.out.println("Status: " + health.getStatus());
        System.out.println("Healthy: " + health.isHealthy());
    }
    
    /**
     * Performs a simple extraction without any special configuration.
     */
    private static void performSimpleExtraction(KreuzbergClient client, File file) throws KreuzbergException {
        System.out.println("Extracting from: " + file.getName());
        
        List<ExtractionResult> results = client.extractFile(file);
        
        if (results.isEmpty()) {
            System.out.println("No results returned");
            return;
        }
        
        ExtractionResult result = results.get(0);
        
        System.out.println("MIME Type: " + result.getMimeType());
        
        if (result.getMetadata() != null) {
            if (result.getMetadata().getPages() != null) {
                System.out.println("Pages: " + result.getMetadata().getPages());
            }
            if (result.getMetadata().getTitle() != null) {
                System.out.println("Title: " + result.getMetadata().getTitle());
            }
        }
        
        String content = result.getContent();
        if (content != null) {
            System.out.println("Content length: " + content.length() + " characters");
            System.out.println("First 200 characters:");
            System.out.println(content.substring(0, Math.min(200, content.length())));
            if (content.length() > 200) {
                System.out.println("...");
            }
        }
    }
    
    /**
     * Performs extraction with keyword and entity extraction enabled.
     */
    private static void performConfiguredExtraction(KreuzbergClient client, File file) throws KreuzbergException {
        System.out.println("Extracting from: " + file.getName());
        
        // Build configuration
        ExtractionConfig config = ExtractionConfig.builder()
                .extractKeywords(true)
                .keywordCount(10)
                .extractEntities(true)
                .extractImages(true)
                .extractTables(true)
                .autoDetectLanguage(true)
                .build();
        
        List<ExtractionResult> results = client.extractFile(config, file);
        
        if (results.isEmpty()) {
            System.out.println("No results returned");
            return;
        }
        
        ExtractionResult result = results.get(0);
        
        // Display keywords
        if (result.getKeywords() != null && !result.getKeywords().isEmpty()) {
            System.out.println("Keywords:");
            result.getKeywords().forEach(keyword ->
                System.out.printf("  - %s (score: %.4f)%n", keyword.getTerm(), keyword.getScore())
            );
        } else {
            System.out.println("No keywords extracted");
        }
        
        System.out.println();
        
        // Display entities
        if (result.getEntities() != null && !result.getEntities().isEmpty()) {
            System.out.println("Entities:");
            result.getEntities().forEach(entity -> 
                System.out.println("  - " + entity)
            );
        } else {
            System.out.println("No entities extracted");
        }
        if (result.getImages() != null && !result.getImages().isEmpty()) {
            System.out.println("Images:");
            result.getImages().forEach(image ->
                    System.out.println("  - " + image.getFilename())
            );
        } else {
            System.out.println("No Images extracted");
        }
        if (result.getTables() != null && !result.getTables().isEmpty()) {
            System.out.println("Tables:");
            result.getTables().forEach(table ->
                    System.out.println("  - " + table)
            );
        } else {
            System.out.println("No Images extracted");
        }
        
        System.out.println();
        
        // Display detected languages
        if (result.getDetectedLanguages() != null && !result.getDetectedLanguages().isEmpty()) {
            System.out.println("Detected languages: " + String.join(", ", result.getDetectedLanguages()));
        }
    }
}

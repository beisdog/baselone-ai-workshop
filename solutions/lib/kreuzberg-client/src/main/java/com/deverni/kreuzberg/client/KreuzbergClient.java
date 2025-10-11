package com.deverni.kreuzberg.client;

import com.deverni.kreuzberg.client.exception.KreuzbergException;
import com.deverni.kreuzberg.client.model.ErrorResponse;
import com.deverni.kreuzberg.client.model.ExtractionConfig;
import com.deverni.kreuzberg.client.model.ExtractionResult;
import com.deverni.kreuzberg.client.model.HealthResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Java REST client for the Kreuzberg document extraction API.
 * 
 * <p>This client provides methods to:
 * <ul>
 *   <li>Extract text from documents (single or batch)</li>
 *   <li>Configure extraction options via query parameters or headers</li>
 *   <li>Check API health status</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * KreuzbergClient client = new KreuzbergClient("http://localhost:8000");
 * 
 * // Simple extraction
 * List<ExtractionResult> results = client.extractFiles(new File("document.pdf"));
 * 
 * // Extraction with configuration
 * ExtractionConfig config = ExtractionConfig.builder()
 *     .extractKeywords(true)
 *     .forceOcr(true)
 *     .ocrBackend("tesseract")
 *     .build();
 * results = client.extractFiles(config, new File("image.jpg"));
 * 
 * // Health check
 * if (client.healthCheck().isHealthy()) {
 *     System.out.println("API is healthy");
 * }
 * }</pre>
 */
public class KreuzbergClient implements AutoCloseable {
    
    private static final String DEFAULT_BASE_URL = "http://localhost:8000";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json; charset=utf-8");
    
    private final String baseUrl;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    /**
     * Creates a new Kreuzberg client with default base URL (http://localhost:8000).
     */
    public KreuzbergClient() {
        this(DEFAULT_BASE_URL);
    }
    
    /**
     * Creates a new Kreuzberg client with the specified base URL.
     * 
     * @param baseUrl the base URL of the Kreuzberg API server
     */
    public KreuzbergClient(String baseUrl) {
        this(baseUrl, createDefaultHttpClient());
    }
    
    /**
     * Creates a new Kreuzberg client with custom OkHttpClient.
     * 
     * @param baseUrl the base URL of the Kreuzberg API server
     * @param httpClient custom configured OkHttpClient
     */
    public KreuzbergClient(String baseUrl, OkHttpClient httpClient) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.httpClient = httpClient;
        this.objectMapper = createObjectMapper();
    }
    
    /**
     * Creates an ObjectMapper with appropriate stream constraints for handling large documents.
     * This prevents Jackson parsing errors when dealing with large extracted content.
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Configure stream constraints to handle large documents
        // Kreuzberg can return very large text content from document extraction
        mapper.getFactory().setStreamReadConstraints(
            com.fasterxml.jackson.core.StreamReadConstraints.builder()
                .maxStringLength(100_000_000)  // 100MB max string length for large documents
                .maxNumberLength(10_000)        // 10K max number length
                .maxNestingDepth(2_000)         // 2K max nesting depth
                .build()
        );
        
        return mapper;
    }
    
    /**
     * Creates a default OkHttpClient with sensible timeouts.
     */
    private static OkHttpClient createDefaultHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS) // Longer timeout for large file processing
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * Performs a health check on the Kreuzberg API.
     * 
     * @return HealthResponse containing the health status
     * @throws KreuzbergException if the health check fails
     */
    public HealthResponse healthCheck() throws KreuzbergException {
        Request request = new Request.Builder()
                .url(baseUrl + "/health")
                .get()
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new KreuzbergException(response.code(), "Health check failed");
            }
            
            ResponseBody body = response.body();
            if (body == null) {
                throw new KreuzbergException("Empty response body from health check");
            }
            
            return objectMapper.readValue(body.string(), HealthResponse.class);
        } catch (IOException e) {
            throw new KreuzbergException("Failed to perform health check", e);
        }
    }
    
    /**
     * Extracts content from a single file.
     * 
     * @param file the file to extract content from
     * @return list of extraction results (typically one result for one file)
     * @throws KreuzbergException if extraction fails
     */
    public List<ExtractionResult> extractFile(File file) throws KreuzbergException {
        return extractFilesWithConfig(null, file);
    }
    
    /**
     * Extracts content from multiple files in a single request.
     * 
     * @param files the files to extract content from
     * @return list of extraction results (one per file)
     * @throws KreuzbergException if extraction fails
     */
    public List<ExtractionResult> extractFiles(File... files) throws KreuzbergException {
        return extractFilesWithConfig(null, files);
    }
    
    /**
     * Extracts content from a single file with custom configuration.
     * 
     * @param config extraction configuration options
     * @param file the file to extract content from
     * @return list of extraction results
     * @throws KreuzbergException if extraction fails
     */
    public List<ExtractionResult> extractFile(ExtractionConfig config, File file) throws KreuzbergException {
        return extractFilesWithConfig(config, file);
    }
    
    /**
     * Extracts content from multiple files with custom configuration.
     * 
     * @param config extraction configuration options (can be null)
     * @param files the files to extract content from
     * @return list of extraction results (one per file)
     * @throws KreuzbergException if extraction fails
     */
    public List<ExtractionResult> extractFilesWithConfig(ExtractionConfig config, File... files) throws KreuzbergException {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("At least one file must be provided");
        }
        
        // Build multipart request body
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        
        for (File file : files) {
            if (!file.exists()) {
                throw new IllegalArgumentException("File does not exist: " + file.getAbsolutePath());
            }
            if (!file.canRead()) {
                throw new IllegalArgumentException("File is not readable: " + file.getAbsolutePath());
            }
            
            String mimeType = detectMimeType(file);
            bodyBuilder.addFormDataPart(
                    "data",
                    file.getName(),
                    RequestBody.create(file, MediaType.parse(mimeType))
            );
        }
        
        RequestBody requestBody = bodyBuilder.build();
        
        // Build URL with query parameters if using simple config
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/extract").newBuilder();
        
        // Build request
        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .post(requestBody);
        
        // Add configuration via header if provided
        if (config != null) {
            try {
                String configJson = objectMapper.writeValueAsString(config);
                requestBuilder.addHeader("X-Extraction-Config", configJson);
            } catch (IOException e) {
                throw new KreuzbergException("Failed to serialize extraction config", e);
            }
        }
        
        Request request = requestBuilder.build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                throw new KreuzbergException(response.code(), "Empty response body");
            }
            
            String responseBody = body.string();
            //Files.writeString(Path.of("./kreuzberg.json"),responseBody, StandardOpenOption.CREATE);

            if (!response.isSuccessful()) {
                ErrorResponse errorResponse = null;
                try {
                    errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                } catch (IOException e) {
                    // Failed to parse error response, use raw body
                }
                
                if (errorResponse != null) {
                    throw new KreuzbergException(response.code(), errorResponse);
                } else {
                    throw new KreuzbergException(response.code(), responseBody);
                }
            }
            
            // Parse successful response
            return objectMapper.readValue(responseBody, new TypeReference<List<ExtractionResult>>() {});
            
        } catch (IOException e) {
            throw new KreuzbergException("Failed to extract files", e);
        }
    }
    
    /**
     * Extracts content from a byte array with specified filename.
     * 
     * @param data the file data
     * @param filename the filename (used to detect content type)
     * @return list of extraction results
     * @throws KreuzbergException if extraction fails
     */
    public List<ExtractionResult> extractBytes(byte[] data, String filename) throws KreuzbergException {
        return extractBytes(null, data, filename);
    }
    
    /**
     * Extracts content from a byte array with custom configuration.
     * 
     * @param config extraction configuration options (can be null)
     * @param data the file data
     * @param filename the filename (used to detect content type)
     * @return list of extraction results
     * @throws KreuzbergException if extraction fails
     */
    public List<ExtractionResult> extractBytes(ExtractionConfig config, byte[] data, String filename) 
            throws KreuzbergException {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        
        String mimeType = detectMimeTypeFromFilename(filename);
        
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "data",
                        filename,
                        RequestBody.create(data, MediaType.parse(mimeType))
                )
                .build();
        
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/extract").newBuilder();
        
        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .post(requestBody);
        
        if (config != null) {
            try {
                String configJson = objectMapper.writeValueAsString(config);
                requestBuilder.addHeader("X-Extraction-Config", configJson);
            } catch (IOException e) {
                throw new KreuzbergException("Failed to serialize extraction config", e);
            }
        }
        
        Request request = requestBuilder.build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                throw new KreuzbergException(response.code(), "Empty response body");
            }
            
            String responseBody = body.string();
            
            if (!response.isSuccessful()) {
                ErrorResponse errorResponse = null;
                try {
                    errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                } catch (IOException e) {
                    // Failed to parse error response
                }
                
                if (errorResponse != null) {
                    throw new KreuzbergException(response.code(), errorResponse);
                } else {
                    throw new KreuzbergException(response.code(), responseBody);
                }
            }
            
            return objectMapper.readValue(responseBody, new TypeReference<List<ExtractionResult>>() {});
            
        } catch (IOException e) {
            throw new KreuzbergException("Failed to extract bytes", e);
        }
    }
    
    /**
     * Extracts content from an InputStream with specified filename.
     * 
     * @param inputStream the input stream containing file data
     * @param filename the filename (used to detect content type)
     * @return list of extraction results
     * @throws KreuzbergException if extraction fails
     */
    public List<ExtractionResult> extractStream(InputStream inputStream, String filename) 
            throws KreuzbergException {
        return extractStream(null, inputStream, filename);
    }
    
    /**
     * Extracts content from an InputStream with custom configuration.
     * 
     * @param config extraction configuration options (can be null)
     * @param inputStream the input stream containing file data
     * @param filename the filename (used to detect content type)
     * @return list of extraction results
     * @throws KreuzbergException if extraction fails
     */
    public List<ExtractionResult> extractStream(ExtractionConfig config, InputStream inputStream, String filename) 
            throws KreuzbergException {
        try {
            byte[] data = inputStream.readAllBytes();
            return extractBytes(config, data, filename);
        } catch (IOException e) {
            throw new KreuzbergException("Failed to read input stream", e);
        }
    }
    
    /**
     * Detects MIME type from a file.
     */
    private String detectMimeType(File file) {
        try {
            String mimeType = Files.probeContentType(file.toPath());
            return mimeType != null ? mimeType : "application/octet-stream";
        } catch (IOException e) {
            return detectMimeTypeFromFilename(file.getName());
        }
    }
    
    /**
     * Detects MIME type from filename extension.
     */
    private String detectMimeTypeFromFilename(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (lower.endsWith(".doc")) return "application/msword";
        if (lower.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (lower.endsWith(".xls")) return "application/vnd.ms-excel";
        if (lower.endsWith(".pptx")) return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        if (lower.endsWith(".ppt")) return "application/vnd.ms-powerpoint";
        if (lower.endsWith(".txt")) return "text/plain";
        if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".tiff") || lower.endsWith(".tif")) return "image/tiff";
        if (lower.endsWith(".bmp")) return "image/bmp";
        return "application/octet-stream";
    }
    
    /**
     * Returns the base URL of the Kreuzberg API.
     */
    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * Returns the underlying OkHttpClient.
     */
    public OkHttpClient getHttpClient() {
        return httpClient;
    }
    
    /**
     * Returns the ObjectMapper used for JSON serialization/deserialization.
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
    
    @Override
    public void close() {
        // Close the connection pool and executor service
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
}

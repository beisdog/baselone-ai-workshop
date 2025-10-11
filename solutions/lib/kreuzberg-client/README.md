# Kreuzberg REST Client

A Java REST client for the [Kreuzberg](https://kreuzberg.dev/) document extraction API using OkHttp.

## Overview

This client provides a simple and type-safe way to interact with the Kreuzberg document intelligence API from Java applications. It supports text extraction from various document formats including PDFs, images, Office documents, and more.

## Features

- ✅ Simple and intuitive API
- ✅ Full support for Kreuzberg extraction features
- ✅ Synchronous operations using OkHttp
- ✅ Type-safe configuration with builder pattern
- ✅ Comprehensive error handling
- ✅ Support for single and batch file extraction
- ✅ Multiple input types (File, byte[], InputStream)
- ✅ Health check endpoint
- ✅ Automatic MIME type detection
- ✅ Pre-configured for large documents (100MB+ content)

## Requirements

- Java 17 or higher
- Maven 3.6+
- Running Kreuzberg API server

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.deverni</groupId>
    <artifactId>kreuzberg-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

Or build from source:

```bash
mvn clean install
```

## Quick Start

### Basic Usage

```java
import com.deverni.kreuzberg.client.KreuzbergClient;
import com.deverni.kreuzberg.client.model.ExtractionResult;

import java.io.File;
import java.util.List;

public class Example {
    public static void main(String[] args) throws Exception {
        // Create client (defaults to http://localhost:8000)
        try (KreuzbergClient client = new KreuzbergClient()) {
            
            // Extract text from a PDF
            File pdf = new File("document.pdf");
            List<ExtractionResult> results = client.extractFile(pdf);
            
            // Access extracted content
            ExtractionResult result = results.get(0);
            System.out.println("Content: " + result.getContent());
            System.out.println("MIME Type: " + result.getMimeType());
            System.out.println("Pages: " + result.getMetadata().getPages());
        }
    }
}
```

### With Custom Configuration

```java
import com.deverni.kreuzberg.client.KreuzbergClient;
import com.deverni.kreuzberg.client.model.ExtractionConfig;
import com.deverni.kreuzberg.client.model.ExtractionResult;

import java.io.File;
import java.util.List;

public class ConfiguredExample {
    public static void main(String[] args) throws Exception {
        try (KreuzbergClient client = new KreuzbergClient("http://localhost:8000")) {
            
            // Build extraction configuration
            ExtractionConfig config = ExtractionConfig.builder()
                    .extractKeywords(true)
                    .keywordCount(10)
                    .extractEntities(true)
                    .forceOcr(true)
                    .ocrBackend("tesseract")
                    .autoDetectLanguage(true)
                    .build();
            
            // Extract with configuration
            List<ExtractionResult> results = client.extractFile(config, new File("image.jpg"));
            
            ExtractionResult result = results.get(0);
            System.out.println("Keywords: " + result.getKeywords());
            System.out.println("Entities: " + result.getEntities());
        }
    }
}
```

### Batch Processing

```java
import com.deverni.kreuzberg.client.KreuzbergClient;
import com.deverni.kreuzberg.client.model.ExtractionResult;

import java.io.File;
import java.util.List;

public class BatchExample {
    public static void main(String[] args) throws Exception {
        try (KreuzbergClient client = new KreuzbergClient()) {

            // Extract from multiple files at once
            List<ExtractionResult> results = client.extractFilesWithConfig(
                    new File("document1.pdf"),
                    new File("document2.docx"),
                    new File("image.jpg")
            );

            // Process each result
            for (int i = 0; i < results.size(); i++) {
                System.out.println("File " + (i + 1) + ": " + results.get(i).getContent());
            }
        }
    }
}
```

### Image Extraction and OCR

```java
import com.deverni.kreuzberg.client.KreuzbergClient;
import com.deverni.kreuzberg.client.model.ExtractionConfig;
import com.deverni.kreuzberg.client.model.ExtractionResult;
import com.deverni.kreuzberg.client.model.ImageData;

import java.io.File;
import java.util.List;

public class ImageExample {
    public static void main(String[] args) throws Exception {
        try (KreuzbergClient client = new KreuzbergClient()) {
            
            // Configure image extraction with OCR
            ExtractionConfig config = ExtractionConfig.builder()
                    .extractImages(true)
                    .ocrExtractedImages(true)
                    .imageOcrBackend("tesseract")
                    .imageOcrMinDimensions(200, 200)
                    .imageOcrMaxDimensions(4000, 4000)
                    .deduplicateImages(true)
                    .build();
            
            List<ExtractionResult> results = client.extractFile(config, new File("presentation.pptx"));
            
            ExtractionResult result = results.get(0);
            
            // Access extracted images
            List<ImageData> images = result.getImages();
            System.out.println("Extracted " + images.size() + " images");
            
            // Access OCR results from images
            result.getImageOcrResults().forEach(ocrResult -> {
                System.out.println("OCR Confidence: " + ocrResult.getConfidenceScore());
                System.out.println("OCR Text: " + ocrResult.getOcrResult().getContent());
            });
        }
    }
}
```

### Working with Streams

```java
import com.deverni.kreuzberg.client.KreuzbergClient;
import com.deverni.kreuzberg.client.model.ExtractionResult;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class StreamExample {
    public static void main(String[] args) throws Exception {
        try (KreuzbergClient client = new KreuzbergClient();
             InputStream inputStream = new FileInputStream("document.pdf")) {
            
            // Extract from InputStream
            List<ExtractionResult> results = client.extractStream(inputStream, "document.pdf");
            
            System.out.println("Content: " + results.get(0).getContent());
        }
    }
}
```

### Health Check

```java
import com.deverni.kreuzberg.client.KreuzbergClient;
import com.deverni.kreuzberg.client.model.HealthResponse;

public class HealthCheckExample {
    public static void main(String[] args) throws Exception {
        try (KreuzbergClient client = new KreuzbergClient()) {
            HealthResponse health = client.healthCheck();
            
            if (health.isHealthy()) {
                System.out.println("API is healthy!");
            } else {
                System.out.println("API is not healthy: " + health.getStatus());
            }
        }
    }
}
```

## Configuration Options

The `ExtractionConfig` class supports all Kreuzberg API configuration options:

### Content Processing
- `chunkContent(boolean)` - Enable content chunking
- `maxChars(int)` - Maximum characters per chunk
- `maxOverlap(int)` - Overlap between chunks

### Feature Extraction
- `extractTables(boolean)` - Extract tables from documents
- `extractEntities(boolean)` - Extract named entities
- `extractKeywords(boolean)` - Extract keywords
- `keywordCount(int)` - Number of keywords to extract

### OCR Configuration
- `forceOcr(boolean)` - Force OCR processing
- `ocrBackend(String)` - OCR engine: "tesseract", "easyocr", "paddleocr"
- `autoDetectLanguage(boolean)` - Automatically detect document language

### Image Processing
- `extractImages(boolean)` - Extract embedded images
- `ocrExtractedImages(boolean)` - Run OCR on extracted images
- `imageOcrBackend(String)` - OCR engine for images
- `imageOcrMinDimensions(width, height)` - Minimum image dimensions for OCR
- `imageOcrMaxDimensions(width, height)` - Maximum image dimensions for OCR
- `deduplicateImages(boolean)` - Remove duplicate images

### Document Security
- `pdfPassword(String)` - Password for encrypted PDFs

## Error Handling

The client throws `KreuzbergException` for API errors:

```java
import com.deverni.kreuzberg.client.KreuzbergClient;
import com.deverni.kreuzberg.client.exception.KreuzbergException;

try (KreuzbergClient client = new KreuzbergClient()) {
    client.extractFile(new File("document.pdf"));
} catch (KreuzbergException e) {
    System.err.println("API Error: " + e.getMessage());
    System.err.println("Status Code: " + e.getStatusCode());
    
    if (e.isClientError()) {
        System.err.println("Client error (4xx)");
    } else if (e.isServerError()) {
        System.err.println("Server error (5xx)");
    }
    
    if (e.getErrorResponse() != null) {
        System.err.println("Details: " + e.getErrorResponse().getDetails());
    }
}
```

## Custom HTTP Client

You can provide a custom configured OkHttpClient:

```java
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

OkHttpClient customClient = new OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .addInterceptor(chain -> {
            // Add custom headers or logging
            return chain.proceed(chain.request());
        })
        .build();

KreuzbergClient client = new KreuzbergClient("http://localhost:8000", customClient);
```

## Supported Document Formats

- PDF documents
- Microsoft Word (DOC, DOCX)
- Microsoft Excel (XLS, XLSX)
- Microsoft PowerPoint (PPT, PPTX)
- Images (JPEG, PNG, TIFF, BMP, GIF)
- HTML documents
- Plain text files
- Email files (EML, MSG)
- And more...

## API Reference

### KreuzbergClient Methods

- `healthCheck()` - Check API health status
- `extractFile(File)` - Extract from single file
- `extractFiles(File...)` - Extract from multiple files
- `extractFile(ExtractionConfig, File)` - Extract with configuration
- `extractFiles(ExtractionConfig, File...)` - Batch extract with configuration
- `extractBytes(byte[], String)` - Extract from byte array
- `extractBytes(ExtractionConfig, byte[], String)` - Extract bytes with configuration
- `extractStream(InputStream, String)` - Extract from input stream
- `extractStream(ExtractionConfig, InputStream, String)` - Extract stream with configuration

## Running Kreuzberg Server

To use this client, you need a running Kreuzberg API server:

### Using Docker

```bash
# Basic server with Tesseract OCR
docker run -p 8000:8000 goldziher/kreuzberg

# Full server with all features
docker run -p 8000:8000 goldziher/kreuzberg-core:latest
```

### Using Python

```bash
# Install Kreuzberg
pip install kreuzberg[all]

# Start the API server
litestar --app kreuzberg._api.main:app run
```

## Building from Source

```bash
git clone <repository-url>
cd kreuzberg-client
mvn clean install
```

## Testing

Run the test suite:

```bash
mvn test
```

## Troubleshooting

### Large Document Support

The client is pre-configured to handle documents up to 100MB. If you're processing very large documents:

1. **Increase JVM heap size**:
   ```bash
   java -Xmx4g -jar your-app.jar
   ```

2. **Use chunking** to process documents in smaller pieces:
   ```java
   ExtractionConfig config = ExtractionConfig.builder()
       .chunkContent(true)
       .maxChars(5000)
       .build();
   ```

3. See [STREAM_CONSTRAINTS.md](STREAM_CONSTRAINTS.md) for detailed information.

### Keyword Mapping

Keywords are returned as `List<Keyword>` (not `Map<String, Double>`). See [KEYWORDS_MAPPING.md](KEYWORDS_MAPPING.md) for usage examples.

### Connection Timeouts

For very large files, you may need to increase timeouts:

```java
OkHttpClient customClient = new OkHttpClient.Builder()
    .readTimeout(300, TimeUnit.SECONDS)  // 5 minutes
    .writeTimeout(300, TimeUnit.SECONDS)
    .build();

KreuzbergClient client = new KreuzbergClient("http://localhost:8000", customClient);
```

## License

This project is licensed under the same terms as the Kreuzberg project.

## Links

- [Kreuzberg Documentation](https://kreuzberg.dev/)
- [Kreuzberg GitHub](https://github.com/Goldziher/kreuzberg)
- [API Server Documentation](https://kreuzberg.dev/user-guide/api-server/)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

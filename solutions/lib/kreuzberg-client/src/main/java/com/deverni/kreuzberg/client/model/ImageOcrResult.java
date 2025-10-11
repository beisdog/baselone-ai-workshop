package com.deverni.kreuzberg.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents OCR results from an extracted image.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageOcrResult {
    
    @JsonProperty("image")
    private ImageData image;
    
    @JsonProperty("ocr_result")
    private OcrResult ocrResult;
    
    @JsonProperty("confidence_score")
    private Double confidenceScore;
    
    @JsonProperty("processing_time")
    private Double processingTime;
    
    @JsonProperty("skipped_reason")
    private String skippedReason;

    // Nested OCR result class
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OcrResult {
        @JsonProperty("content")
        private String content;
        
        @JsonProperty("mime_type")
        private String mimeType;
        
        @JsonProperty("metadata")
        private ExtractionMetadata metadata;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public ExtractionMetadata getMetadata() {
            return metadata;
        }

        public void setMetadata(ExtractionMetadata metadata) {
            this.metadata = metadata;
        }
    }

    // Getters and Setters
    
    public ImageData getImage() {
        return image;
    }

    public void setImage(ImageData image) {
        this.image = image;
    }

    public OcrResult getOcrResult() {
        return ocrResult;
    }

    public void setOcrResult(OcrResult ocrResult) {
        this.ocrResult = ocrResult;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Double getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Double processingTime) {
        this.processingTime = processingTime;
    }

    public String getSkippedReason() {
        return skippedReason;
    }

    public void setSkippedReason(String skippedReason) {
        this.skippedReason = skippedReason;
    }
}

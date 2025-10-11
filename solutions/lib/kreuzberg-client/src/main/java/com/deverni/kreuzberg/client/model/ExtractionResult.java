package com.deverni.kreuzberg.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Represents the extraction result from Kreuzberg API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtractionResult {
    
    @JsonProperty("content")
    private String content;
    
    @JsonProperty("mime_type")
    private String mimeType;
    
    @JsonProperty("metadata")
    private ExtractionMetadata metadata;
    
    @JsonProperty("chunks")
    private List<String> chunks;
    
    @JsonProperty("entities")
    private List<String> entities;
    
    @JsonProperty("keywords")
    private List<Keyword> keywords;
    
    @JsonProperty("detected_languages")
    private List<String> detectedLanguages;
    
    @JsonProperty("images")
    private List<ImageData> images;
    
    @JsonProperty("image_ocr_results")
    private List<ImageOcrResult> imageOcrResults;
    
    @JsonProperty("tables")
    private List<Map<String, Object>> tables;

    // Getters and Setters
    
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

    public List<String> getChunks() {
        return chunks;
    }

    public void setChunks(List<String> chunks) {
        this.chunks = chunks;
    }

    public List<String> getEntities() {
        return entities;
    }

    public void setEntities(List<String> entities) {
        this.entities = entities;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public List<String> getDetectedLanguages() {
        return detectedLanguages;
    }

    public void setDetectedLanguages(List<String> detectedLanguages) {
        this.detectedLanguages = detectedLanguages;
    }

    public List<ImageData> getImages() {
        return images;
    }

    public void setImages(List<ImageData> images) {
        this.images = images;
    }

    public List<ImageOcrResult> getImageOcrResults() {
        return imageOcrResults;
    }

    public void setImageOcrResults(List<ImageOcrResult> imageOcrResults) {
        this.imageOcrResults = imageOcrResults;
    }

    public List<Map<String, Object>> getTables() {
        return tables;
    }

    public void setTables(List<Map<String, Object>> tables) {
        this.tables = tables;
    }
}

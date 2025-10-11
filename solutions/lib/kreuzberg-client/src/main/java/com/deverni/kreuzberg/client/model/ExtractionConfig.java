package com.deverni.kreuzberg.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration options for document extraction.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtractionConfig {
    
    @JsonProperty("chunk_content")
    private Boolean chunkContent;
    
    @JsonProperty("max_chars")
    private Integer maxChars;
    
    @JsonProperty("max_overlap")
    private Integer maxOverlap;
    
    @JsonProperty("extract_tables")
    private Boolean extractTables;
    
    @JsonProperty("extract_entities")
    private Boolean extractEntities;
    
    @JsonProperty("extract_keywords")
    private Boolean extractKeywords;
    
    @JsonProperty("keyword_count")
    private Integer keywordCount;
    
    @JsonProperty("force_ocr")
    private Boolean forceOcr;
    
    @JsonProperty("ocr_backend")
    private String ocrBackend; // tesseract, easyocr, paddleocr
    
    @JsonProperty("auto_detect_language")
    private Boolean autoDetectLanguage;
    
    @JsonProperty("pdf_password")
    private String pdfPassword;
    
    @JsonProperty("extract_images")
    private Boolean extractImages;
    
    @JsonProperty("ocr_extracted_images")
    private Boolean ocrExtractedImages;
    
    @JsonProperty("image_ocr_backend")
    private String imageOcrBackend;
    
    @JsonProperty("image_ocr_min_width")
    private Integer imageOcrMinWidth;
    
    @JsonProperty("image_ocr_min_height")
    private Integer imageOcrMinHeight;
    
    @JsonProperty("image_ocr_max_width")
    private Integer imageOcrMaxWidth;
    
    @JsonProperty("image_ocr_max_height")
    private Integer imageOcrMaxHeight;
    
    @JsonProperty("deduplicate_images")
    private Boolean deduplicateImages;

    // Builder pattern
    public static class Builder {
        private final ExtractionConfig config = new ExtractionConfig();
        
        public Builder chunkContent(boolean chunkContent) {
            config.chunkContent = chunkContent;
            return this;
        }
        
        public Builder maxChars(int maxChars) {
            config.maxChars = maxChars;
            return this;
        }
        
        public Builder maxOverlap(int maxOverlap) {
            config.maxOverlap = maxOverlap;
            return this;
        }
        
        public Builder extractTables(boolean extractTables) {
            config.extractTables = extractTables;
            return this;
        }
        
        public Builder extractEntities(boolean extractEntities) {
            config.extractEntities = extractEntities;
            return this;
        }
        
        public Builder extractKeywords(boolean extractKeywords) {
            config.extractKeywords = extractKeywords;
            return this;
        }
        
        public Builder keywordCount(int keywordCount) {
            config.keywordCount = keywordCount;
            return this;
        }
        
        public Builder forceOcr(boolean forceOcr) {
            config.forceOcr = forceOcr;
            return this;
        }
        
        public Builder ocrBackend(String ocrBackend) {
            config.ocrBackend = ocrBackend;
            return this;
        }
        
        public Builder autoDetectLanguage(boolean autoDetectLanguage) {
            config.autoDetectLanguage = autoDetectLanguage;
            return this;
        }
        
        public Builder pdfPassword(String pdfPassword) {
            config.pdfPassword = pdfPassword;
            return this;
        }
        
        public Builder extractImages(boolean extractImages) {
            config.extractImages = extractImages;
            return this;
        }
        
        public Builder ocrExtractedImages(boolean ocrExtractedImages) {
            config.ocrExtractedImages = ocrExtractedImages;
            return this;
        }
        
        public Builder imageOcrBackend(String imageOcrBackend) {
            config.imageOcrBackend = imageOcrBackend;
            return this;
        }
        
        public Builder imageOcrMinDimensions(int width, int height) {
            config.imageOcrMinWidth = width;
            config.imageOcrMinHeight = height;
            return this;
        }
        
        public Builder imageOcrMaxDimensions(int width, int height) {
            config.imageOcrMaxWidth = width;
            config.imageOcrMaxHeight = height;
            return this;
        }
        
        public Builder deduplicateImages(boolean deduplicateImages) {
            config.deduplicateImages = deduplicateImages;
            return this;
        }
        
        public ExtractionConfig build() {
            return config;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }

    // Getters
    
    public Boolean getChunkContent() {
        return chunkContent;
    }

    public Integer getMaxChars() {
        return maxChars;
    }

    public Integer getMaxOverlap() {
        return maxOverlap;
    }

    public Boolean getExtractTables() {
        return extractTables;
    }

    public Boolean getExtractEntities() {
        return extractEntities;
    }

    public Boolean getExtractKeywords() {
        return extractKeywords;
    }

    public Integer getKeywordCount() {
        return keywordCount;
    }

    public Boolean getForceOcr() {
        return forceOcr;
    }

    public String getOcrBackend() {
        return ocrBackend;
    }

    public Boolean getAutoDetectLanguage() {
        return autoDetectLanguage;
    }

    public String getPdfPassword() {
        return pdfPassword;
    }

    public Boolean getExtractImages() {
        return extractImages;
    }

    public Boolean getOcrExtractedImages() {
        return ocrExtractedImages;
    }

    public String getImageOcrBackend() {
        return imageOcrBackend;
    }

    public Integer getImageOcrMinWidth() {
        return imageOcrMinWidth;
    }

    public Integer getImageOcrMinHeight() {
        return imageOcrMinHeight;
    }

    public Integer getImageOcrMaxWidth() {
        return imageOcrMaxWidth;
    }

    public Integer getImageOcrMaxHeight() {
        return imageOcrMaxHeight;
    }

    public Boolean getDeduplicateImages() {
        return deduplicateImages;
    }
}

package com.deverni.kreuzberg.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents an extracted image from a document.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageData {
    
    @JsonProperty("data")
    private String data; // Base64 encoded data URI
    
    @JsonProperty("format")
    private String format;
    
    @JsonProperty("filename")
    private String filename;
    
    @JsonProperty("page_number")
    private Integer pageNumber;
    
    @JsonProperty("dimensions")
    private List<Integer> dimensions; // [width, height]
    
    @JsonProperty("colorspace")
    private String colorspace;
    
    @JsonProperty("bits_per_component")
    private Integer bitsPerComponent;
    
    @JsonProperty("is_mask")
    private Boolean isMask;
    
    @JsonProperty("description")
    private String description;

    // Getters and Setters
    
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<Integer> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<Integer> dimensions) {
        this.dimensions = dimensions;
    }

    public String getColorspace() {
        return colorspace;
    }

    public void setColorspace(String colorspace) {
        this.colorspace = colorspace;
    }

    public Integer getBitsPerComponent() {
        return bitsPerComponent;
    }

    public void setBitsPerComponent(Integer bitsPerComponent) {
        this.bitsPerComponent = bitsPerComponent;
    }

    public Boolean getIsMask() {
        return isMask;
    }

    public void setIsMask(Boolean isMask) {
        this.isMask = isMask;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

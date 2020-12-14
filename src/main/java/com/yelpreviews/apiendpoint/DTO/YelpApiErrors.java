package com.yelpreviews.apiendpoint.DTO;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class YelpApiErrors {
    private List<Map<String,String>> errors;

    public YelpApiErrors() {}

    /**
     * Getters & Setters
     */
    @JsonProperty("errors")
    public List<Map<String,String>> getErrors() { return this.errors; }
    @JsonAlias({ "errors", "error" })
    public void setErrors(List<Map<String,String>> errors) { this.errors = errors; }
}
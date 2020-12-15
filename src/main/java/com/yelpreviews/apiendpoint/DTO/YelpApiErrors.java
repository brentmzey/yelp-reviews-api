package com.yelpreviews.apiendpoint.DTO;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class YelpApiErrors {
    private List<Map<String,String>> errors;

    public YelpApiErrors() {}

    public YelpApiErrors(Map<String, String> errors) {
        this.errors = List.of(errors);
    }

    /**
     * Getters & Setters
     */
    @JsonAlias({ "errors", "error" })
    public List<Map<String,String>> getErrors() { return this.errors; }
    @JsonAlias({ "errors", "error" })
    public void setErrors(List<Map<String,String>> errors) { this.errors = errors; }
}
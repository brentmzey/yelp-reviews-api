package com.yelpreviews.apiendpoint.DTO;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.http.HttpStatus;

@JsonInclude(Include.NON_NULL)
public class YelpApiError extends ApiErrorListElement {

    private HttpStatus statusCode;
    private String message;

    public YelpApiError() {}

    public YelpApiError(HttpStatus statusCode, String searchParameter, Object rejectedValue, String message) {
        super(ErrorType.YELP_API_ERROR, message, searchParameter, rejectedValue);
        this.statusCode = statusCode;
    }

    /**
     * Getters & Setters
     */
    @JsonGetter("statusCode")
    public HttpStatus getStatusCode() { return this.statusCode; }
    @JsonGetter("message")
    public String getMessage() { return this.message; }
    
    @JsonSetter("error")
    public void setYelpApiError(Map<String,String> errors) {
        this.statusCode = HttpStatus.valueOf(errors.get("code"));
        this.message = errors.get("description");
    }
}
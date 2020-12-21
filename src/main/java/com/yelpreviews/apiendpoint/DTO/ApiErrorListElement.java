package com.yelpreviews.apiendpoint.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "errorType", "field", "rejectedValue", "message" })
public abstract class ApiErrorListElement {

    private ErrorType errorType;
    private String searchParameter;
    private Object rejectedValue;
    private String message;

    public ApiErrorListElement() {}

    public ApiErrorListElement(ErrorType errorType) {
        this.errorType = errorType;
    }
    
    public ApiErrorListElement(ErrorType errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }
    
    public ApiErrorListElement(ErrorType errorType, String message, String searchParameter, Object rejectedValue) {
        this(errorType, message);
        this.searchParameter = searchParameter;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }

    @JsonProperty("errorType")
    public String getErrorType() { return this.errorType.name(); }
    @JsonProperty("field")
    private String getSearchParameter() {return this.searchParameter;};
    @JsonProperty("rejectedValue")
    private Object getRejectedValue() {return this.rejectedValue;};
    @JsonProperty("message")
    private String getMessage() {return this.message;};

}
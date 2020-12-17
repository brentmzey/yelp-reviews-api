package com.yelpreviews.apiendpoint.DTO;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yelpreviews.apiendpoint.utils.JSON;
import org.springframework.http.HttpStatus;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "statusCode", "status", "message", "errors" })
public class ApiError {

    private int statusCode;
    private HttpStatus status;
    private String message;
    private List<ApiErrorListElement> errors;
    @JsonIgnore
    private String debugMessage;

    public String toJson() throws JsonProcessingException { return JSON.toJson(this); }

    public ApiError() {}

    public ApiError(HttpStatus httpStatus) {
        this.status = httpStatus;
        this.statusCode = httpStatus.value();
    }
    
    public ApiError(HttpStatus httpStatus, String message, List<ApiErrorListElement> errors) {
        this(httpStatus);
        this.message = message;
        this.errors = errors;
    }

    ApiError(HttpStatus httpStatus, List<ApiErrorListElement> errors, Throwable ex) {
        this(httpStatus);
        this.message = "Unexpected error";
        this.errors = errors;
        this.debugMessage = ex.getLocalizedMessage();
    }

    ApiError(HttpStatus httpStatus, String message, List<ApiErrorListElement> errors, Throwable ex) {
        this(httpStatus, message, errors);
        this.debugMessage = ex.getLocalizedMessage();
    }

    /**
     * Getters & Setters
     */
    @JsonProperty("statusCode")
    public int getStatusCode() { return this.statusCode; };
    @JsonProperty("status")
    public HttpStatus getStatus() { return this.status; };
    @JsonProperty("message")
    public String getMessage() { return this.message; };
    @JsonProperty("errors")
    public List<ApiErrorListElement> getErrors() { return this.errors; };
    public String getDebugMessage() { return this.debugMessage; };
    
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; };
    public void seStatus(String status) { this.status = HttpStatus.valueOf(status); };
    public void setMessage(String message) { this.message = message; };
    public void setErrors(List<ApiErrorListElement> errors) { this.errors = errors; };

}
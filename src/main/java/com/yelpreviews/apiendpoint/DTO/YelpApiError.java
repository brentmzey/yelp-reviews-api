package com.yelpreviews.apiendpoint.DTO;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class YelpApiError extends ApiErrorListElement {

    private String errorCode;
    private String message;
    private String searchParameter;
    private Object rejectedValue;

    public YelpApiError() {
      super(ErrorType.YELP_API_ERROR);
    }

    public YelpApiError(String errorCode, String message, String searchParameter, Object rejectedValue) {
        super(ErrorType.YELP_API_ERROR, message, searchParameter, rejectedValue);
        this.errorCode = errorCode;
    }

    /**
     * Getters & Setters
     */
    @JsonGetter("errorCode")
    public String getErrorCode() { return errorCode; }
    @JsonGetter("message")
    public String getMessage() { return message; }
    @JsonGetter("searchParameter")
    public String getSearchParameter() { return searchParameter; }
    @JsonGetter("rejectedValue")
    public Object getRejectedValue() { return rejectedValue; }
    
    @JsonSetter("error")
    public void setYelpApiError(Map<String,?> errors) {
        this.errorCode = (String)errors.get("code");
        this.message = (String)errors.get("description");
    }
    @JsonSetter("code")
    public void setErrorCode(String errorCode) {
      this.errorCode = errorCode;
    }
    @JsonSetter("description")
    public void setMessage(String message) {
      this.message = message;
    }
    @JsonSetter("field")
    public void setSearchParameter(String searchParameter) {
      this.searchParameter = searchParameter;
    }
    @JsonSetter("instance")
    public void setRejectedValue(Object rejectedValue) {
      this.rejectedValue = rejectedValue;
    }
    
}
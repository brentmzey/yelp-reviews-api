package com.yelpreviews.apiendpoint.DTO;

public class WrapperApiError extends ApiErrorListElement {

    public WrapperApiError() {}

    public WrapperApiError(String message) {
        super(ErrorType.WRAPPER_API_ERROR, message);
    }

    public WrapperApiError(String message, String searchParameter) {
        super(ErrorType.WRAPPER_API_ERROR, message, searchParameter, null);
    }
    
    public WrapperApiError(String message, String searchParameter, Object rejectedValue) {
        super(ErrorType.WRAPPER_API_ERROR, message, searchParameter, rejectedValue);
    }
    
}
package com.yelpreviews.apiendpoint.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;

public class InvalidRequestParametersException extends MissingServletRequestParameterException {

    private static final long serialVersionUID = 1L;
    private HttpStatus httpStatus;
    private String message;
    private Object rejectedValue;

    public InvalidRequestParametersException(HttpStatus httpStatus, String message, String searchParameter, String parameterType, Object rejectedValue) {
        super(searchParameter, parameterType);
        this.httpStatus = httpStatus;
        this.message = message;
        this.rejectedValue = rejectedValue;
    }
    
    public InvalidRequestParametersException(HttpStatus httpStatus, String searchParameter, String parameterType, Object rejectedValue) {
        this(httpStatus, "Please try a different search parameter for this term than the rejected value.", searchParameter, parameterType, rejectedValue);
    }

    public HttpStatus getStatus() { return this.httpStatus; }
    public String getMessage() { return this.message; }
    public Object getRejectedValue() { return this.rejectedValue; }

}
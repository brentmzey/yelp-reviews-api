package com.yelpreviews.apiendpoint.exceptions;

import com.yelpreviews.apiendpoint.DTO.YelpApiError;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class YelpApiResponseException extends ResponseStatusException {

    private static final long serialVersionUID = 1L;
    private YelpApiError yelpApiError;

    public YelpApiResponseException(HttpStatus httpStatus, YelpApiError yelpApiError) throws IllegalArgumentException {
        super(httpStatus);
        this.yelpApiError = yelpApiError;
    }

    public YelpApiError getYelpApiError() { return this.yelpApiError; }
    
}
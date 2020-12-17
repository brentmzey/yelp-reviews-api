package com.yelpreviews.apiendpoint.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PathNotFoundException extends ResponseStatusException {

    private static final long serialVersionUID = 1L;

    public PathNotFoundException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }

}
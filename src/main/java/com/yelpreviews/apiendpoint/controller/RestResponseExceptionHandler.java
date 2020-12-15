package com.yelpreviews.apiendpoint.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(value = {ResponseStatusException.class})
	protected ResponseEntity<Object> handle(ResponseStatusException ex, WebRequest request) throws JsonProcessingException {
        return handleExceptionInternal(ex, ex.getReason(), ex.getResponseHeaders(), ex.getStatus(), request);
    }
    
}
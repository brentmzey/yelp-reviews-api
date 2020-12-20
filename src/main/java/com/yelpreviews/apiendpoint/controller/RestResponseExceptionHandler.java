package com.yelpreviews.apiendpoint.controller;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yelpreviews.apiendpoint.DTO.ApiError;
import com.yelpreviews.apiendpoint.DTO.ApiErrorListElement;
import com.yelpreviews.apiendpoint.DTO.WrapperApiError;
import com.yelpreviews.apiendpoint.exceptions.InvalidRequestParametersException;
import com.yelpreviews.apiendpoint.exceptions.PathNotFoundException;
import com.yelpreviews.apiendpoint.exceptions.YelpApiResponseException;
import com.yelpreviews.apiendpoint.utils.JSON;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {
    
    private String buildApiErrorJsonString(HttpStatus status, String message, ApiErrorListElement apiErrorListElement) throws JsonProcessingException {
        return JSON.toJson(new ApiError(status, message, List.of(apiErrorListElement)));
    }
    
    private String buildApiErrorJsonString(HttpStatus status, String message, List<ApiErrorListElement> apiErrorList) throws JsonProcessingException {
        return JSON.toJson(new ApiError(status, message, apiErrorList));
    }

    @ExceptionHandler({ Exception.class })
    protected ResponseEntity<Object> handleInternalErrors(Exception ex, WebRequest request) {
      WrapperApiError internalServerError = new WrapperApiError(ex.getMessage());
      ResponseEntity<Object> response;
      try {
        response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildApiErrorJsonString(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", internalServerError));
      } catch (JsonProcessingException e) {
          throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
      }
      return response;
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
      WrapperApiError internalServerError = new WrapperApiError(ex.getMessage());
      ResponseEntity<Object> response;
      try {
        response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildApiErrorJsonString(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", internalServerError));
      } catch (JsonProcessingException e) {
          throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
      }
      return response;
    }

    @ExceptionHandler({ ServerErrorException.class })
    protected ResponseEntity<Object> handleInternalServerErrors(ServerErrorException ex, WebRequest request) {
      WrapperApiError internalServerError = new WrapperApiError(ex.getMessage());
      ResponseEntity<Object> response;
      try {
        response = ResponseEntity.status(ex.getStatus()).body(buildApiErrorJsonString(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", internalServerError));
      } catch (JsonProcessingException e) {
          throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
      }
      return response;

    }

    @ExceptionHandler(value = {YelpApiResponseException.class})
    protected ResponseEntity<Object> handleYelpApiResponseError(YelpApiResponseException ex, WebRequest request) throws JsonProcessingException {
      return handleExceptionInternal(ex, buildApiErrorJsonString(ex.getStatus(), ex.getReason(), ex.getYelpApiError()), ex.getYelpApiResponseEntity().getHeaders(), ex.getStatus(), request);
    }
    
    @ExceptionHandler(value = {PathNotFoundException.class})
    protected ResponseEntity<Object> handlePathNotFound(PathNotFoundException ex, WebRequest request) throws JsonProcessingException {
      WrapperApiError pathNotFoundError = new WrapperApiError(ex.getReason());
      return handleExceptionInternal(ex, buildApiErrorJsonString(ex.getStatus(), "INVALID_PATH", pathNotFoundError), ex.getResponseHeaders(), ex.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
      StringBuilder supportedMethodsMessage = new StringBuilder(ex.getMethod() + " HTTP method is not supported. Supported HTTP methods to this API are: ");
      ex.getSupportedHttpMethods().forEach(method -> supportedMethodsMessage.append(method + ", "));
      supportedMethodsMessage.replace(supportedMethodsMessage.lastIndexOf(", "), supportedMethodsMessage.length(), ".");
      WrapperApiError httpMethodNotSupported = new WrapperApiError();
      ResponseEntity<Object> response = new ResponseEntity<>(status);
      try {
        response = new ResponseEntity<Object>(buildApiErrorJsonString(status, supportedMethodsMessage.toString(), httpMethodNotSupported), headers, status);
      } catch (JsonProcessingException e) {
        throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
      }
      return response;
    }
    
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
      WrapperApiError invalidRequestParamsError = new WrapperApiError(ex.getParameterName() + " is invalid or missing from the request.", ex.getParameterName());
      ResponseEntity<Object> response = new ResponseEntity<>(status);
      try {
        response = new ResponseEntity<Object>(buildApiErrorJsonString(status, "VALIDATION_ERROR", invalidRequestParamsError), headers, status);
        System.gc();
      } catch (JsonProcessingException e) {
        throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
      }
      return response;
    }

    @ExceptionHandler(value = { InvalidRequestParametersException.class })
    protected ResponseEntity<Object> handleInvalidRequestParameters(InvalidRequestParametersException ex, WebRequest request) throws JsonProcessingException {
      WrapperApiError invalidRequestParamsError = new WrapperApiError(ex.getParameterName() + " is invalid or missing from the request.", ex.getParameterName(), ex.getRejectedValue());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildApiErrorJsonString(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", invalidRequestParamsError));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
    HttpHeaders headers, HttpStatus status, WebRequest request) {
      List<ApiErrorListElement> errors = new ArrayList<ApiErrorListElement>();
      for (FieldError error : ex.getBindingResult().getFieldErrors()) {
        errors.add(new WrapperApiError(error.getDefaultMessage(), error.getField(), ex.getRawFieldValue(error.getField())));
      }
      for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
        errors.add(new WrapperApiError(error.getDefaultMessage(), error.getObjectName(), error.getClass()));
      }
      try {
        return handleExceptionInternal(ex, buildApiErrorJsonString(status, "VALIDATION_ERROR", errors), headers, status, request);
      } catch (JsonProcessingException e) {
        throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
      }
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
        WrapperApiError missingRequestParamsError = new WrapperApiError(ex.getRequestPartName() + " is missing from the request.");
      try {
        return handleExceptionInternal(ex, buildApiErrorJsonString(status, "VALIDATION_ERROR", missingRequestParamsError), headers, status, request);
      } catch (JsonProcessingException e) {
        throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
      }
    }
    
}
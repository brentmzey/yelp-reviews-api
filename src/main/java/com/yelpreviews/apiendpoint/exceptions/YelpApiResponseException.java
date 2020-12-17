package com.yelpreviews.apiendpoint.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.DTO.YelpApiError;
import com.yelpreviews.apiendpoint.utils.JSON;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public class YelpApiResponseException extends ResponseStatusException {

    private static final long serialVersionUID = 1L;
    private ResponseEntity<JsonNode> yelpApiResponseEntity;
    private YelpApiError yelpApiError;

    public YelpApiResponseException(Mono<ResponseEntity<JsonNode>> yelpApiResponseEntity) throws JsonProcessingException, IllegalArgumentException {
        super(yelpApiResponseEntity.block().getStatusCode());
        this.yelpApiResponseEntity = yelpApiResponseEntity.block();
        this.yelpApiError = JSON.jsonToObject(yelpApiResponseEntity.block().getBody(), YelpApiError.class);
    }

    public ResponseEntity<JsonNode> getYelpApiResponseEntity() { return this.yelpApiResponseEntity; }
    public YelpApiError getYelpApiError() { return this.yelpApiError; }
    
}
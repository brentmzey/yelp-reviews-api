package com.yelpreviews.apiendpoint.controller;

import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface ResponseBodyBuilder {
    ResponseEntity<Object> build(ResponseEntity<JsonNode> bizResponseEntity, ResponseEntity<JsonNode> reviewsResponseEntity, Map<String,String> uriVars);
}
package com.yelpreviews.apiendpoint.controller;

import java.util.Map;
import com.yelpreviews.apiendpoint.apis.YelpApi;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface ResponseBodyBuilder {
    Mono<ResponseEntity<Object>> build(YelpApi yelpApi, Map<String,String> uriVars);
}
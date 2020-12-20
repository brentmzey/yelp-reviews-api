package com.yelpreviews.apiendpoint.controller;

import java.util.Map;
import com.yelpreviews.apiendpoint.apis.YelpApi;
import org.springframework.http.ResponseEntity;

public interface ResponseBodyBuilder {
    ResponseEntity<Object> build(YelpApi yelpApi, Map<String,String> uriVars);
}
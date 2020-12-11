package com.yelpreviews.apiendpoint.apis;

import reactor.core.publisher.Mono;

public interface ApiCall {
    public abstract Mono<String> apiCall();
}
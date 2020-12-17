package com.yelpreviews.apiendpoint.controller;

import java.util.Map;

public class ControllerRouteResponseHandlers {

    private Map<String, ResponseBodyBuilder> routeResponseHandlers;

    public ControllerRouteResponseHandlers(Map<String, ResponseBodyBuilder> routeResponseHandlers) {
        this.routeResponseHandlers.putAll(routeResponseHandlers);
    }

    public Map<String, ResponseBodyBuilder> getRouteResponseHandlers() {
        return this.routeResponseHandlers;
    }
    public void setRouteResponseHandlers(Map<String, ResponseBodyBuilder> routeResponseHandlers) {
        this.routeResponseHandlers = routeResponseHandlers;
    }
}
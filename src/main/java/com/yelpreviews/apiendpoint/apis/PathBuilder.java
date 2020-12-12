package com.yelpreviews.apiendpoint.apis;

import java.util.Map;

public interface PathBuilder {
    public abstract String buildPath(Map<String, String> uriVars);
}
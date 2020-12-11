package com.yelpreviews.apiendpoint.apis;

import org.springframework.web.reactive.function.client.WebClient;

public final class YelpApi {
    private static final String YELP_REVIEWS_ROOT_URL = "https://api.yelp.com/v3/businesses"; // THEN: "/{bizId}/reviews"
    private static final String YELP_BIZ_SEARCH_ROOT_URL = "https://api.yelp.com/v3/businesses/search";
    // private ApiCall reviewsSearchApiCall;
    // private ApiCall bizSearchApiCall;

    // public YelpApi(ApiCall reviewsSearchApiCall, ApiCall bizSearchApiCall) {
    //     this.reviewsSearchApiCall = reviewsSearchApiCall;
    //     this.bizSearchApiCall = bizSearchApiCall;
    // }

    public static WebClient getWebClient(String rootUrl) { return WebClient.create(rootUrl); }

    /**
     * Getters
     */
    public static String getYelpReviewsRootUrl() { return YelpApi.YELP_REVIEWS_ROOT_URL; }
    public static String getYelpBizSearchRootUrl() { return YelpApi.YELP_BIZ_SEARCH_ROOT_URL; }
    // public ApiCall getReviewsSearchApiCall() { return this.reviewsSearchApiCall; };
    // public ApiCall getBizSearchApiCall() { return this.bizSearchApiCall; };
    // public void setReviewsSearchApiCall(ApiCall reviewsSearchApiCall) { this.reviewsSearchApiCall = reviewsSearchApiCall; };
    // public void setBizSearchApiCall(ApiCall bizSearchApiCall) { this.bizSearchApiCall = bizSearchApiCall; }
}
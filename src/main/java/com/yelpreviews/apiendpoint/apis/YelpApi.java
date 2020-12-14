package com.yelpreviews.apiendpoint.apis;

import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.DTO.YelpApiErrors;
import com.yelpreviews.apiendpoint.exceptions.IncorrectDotEnvFileFormat;
import com.yelpreviews.apiendpoint.utils.DotEnvFileToSysProps;
import com.yelpreviews.apiendpoint.utils.JSON;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class YelpApi {
    public enum CallType { REVIEWS, BUSINESS; }
    private final String YELP_API_ROOT_URL = "https://api.yelp.com/v3/businesses"; // THEN: "/{bizId}/reviews"
    private PathBuilder reviewsSearchUriBuilder;
    private PathBuilder bizSearchUriBuilder;
    private HttpMethod httpMethod;
    private Map<String,String> uriVars;
    private String API_KEY = System.getenv().get("YELP_API_KEY");

    {
        if(API_KEY == null) {
            try {
            // Grab Yelp API key from .env in root directory
              DotEnvFileToSysProps.setCredentialsAsSystemProperties();
              API_KEY = System.getProperty("YELP_API_KEY");
            } catch (IncorrectDotEnvFileFormat e) {
              System.out.println(e.getMessage());
            }
         }
    }

    public Mono<String> apiCall(CallType callType, PathBuilder uriBuilderFnc, Map<String,String> uriVars, HttpMethod httpMethod) throws JsonProcessingException {
        return this.getWebClient(this.YELP_API_ROOT_URL)
                    .method(httpMethod)
                    .uri(uriBuilderFnc.buildPath(uriVars))
                    .header("Authorization", "Bearer " + API_KEY)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(res -> {
                            if (res.statusCode().equals(HttpStatus.OK)) {
                                return res.bodyToMono(String.class);
                            }
                            else if (res.statusCode().is4xxClientError() || res.statusCode().is5xxServerError()) {
                                try {
                                  return Mono.just(JSON.toJson(res.bodyToMono(YelpApiErrors.class)));
                                } catch (JsonProcessingException e) {
                                  System.out.println(e.getMessage());
                                }
                            }
                            throw res.createException().block();
                        }
                    );
    }

    public JsonNode toJsonNode(CallType callType, String apiCallResultString, boolean isBizDetailsSingleton) throws JsonMappingException, JsonProcessingException {
        switch (callType) {
            case REVIEWS:
                return JSON.parseJsonString(apiCallResultString);
            case BUSINESS:
                if(isBizDetailsSingleton) {
                    return JSON.parseJsonString("{ \"businesses\": [" + apiCallResultString + "]}");
                }
                return JSON.parseJsonString(apiCallResultString);
            default:
                return JSON.parseJsonString(apiCallResultString);
        }
    }


    /**
     * Fewer args constructor when it is not required to prefetch Yelp Business Id and details before fetching the reviews. This is the constructor used when the Yelp business id is pre-specified in the URL path and passed in from the controller function
     * @param reviewsSearchUriBuilder
     * @param bizSearchUriBuilder
     * @param httpMethod
     * @param uriVars
     * @throws JsonMappingException
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     */
    public YelpApi(PathBuilder reviewsSearchUriBuilder, PathBuilder bizSearchUriBuilder, HttpMethod httpMethod, Map<String,String> uriVars) throws JsonMappingException, JsonProcessingException, IllegalArgumentException {
        this.reviewsSearchUriBuilder = reviewsSearchUriBuilder;
        this.bizSearchUriBuilder = bizSearchUriBuilder;
        this.httpMethod = httpMethod;
        this.uriVars = uriVars;  
    }

    /**
     * Getters
     */
    public WebClient getWebClient(String rootUrl) { return WebClient.create(rootUrl); }
    public String getRootUrl() { return this.YELP_API_ROOT_URL; }
    public PathBuilder getReviewsSearchUriBuilder() { return this.reviewsSearchUriBuilder; };
    public PathBuilder getBizSearchUriBuilder() { return this.bizSearchUriBuilder; };
    public HttpMethod getHttpMethod() { return this.httpMethod; };
    public Map<String,String> getUriVars() { return this.uriVars; };
        
}
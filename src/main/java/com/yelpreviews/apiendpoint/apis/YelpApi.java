package com.yelpreviews.apiendpoint.apis;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.DTO.ApiError;
import com.yelpreviews.apiendpoint.DTO.WrapperApiError;
import com.yelpreviews.apiendpoint.controller.ControllerConfig;
import com.yelpreviews.apiendpoint.controller.Controller.RouteType;
import com.yelpreviews.apiendpoint.exceptions.IncorrectDotEnvFileFormat;
import com.yelpreviews.apiendpoint.exceptions.YelpApiResponseException;
import com.yelpreviews.apiendpoint.utils.DotEnvFileToSysProps;
import com.yelpreviews.apiendpoint.utils.JSON;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
              DotEnvFileToSysProps.setCredentialsAsSystemProperties();
              API_KEY = System.getProperty("YELP_API_KEY");
            } catch (IncorrectDotEnvFileFormat e) {
              System.out.println(e.getMessage());
            }
         }
    }

    /**
     * Interfacing method to call the Yelp API
     * @param callType
     * @param uriBuilderFnc
     * @param uriVars
     * @param httpMethod
     * @return Mono<String> offering unbounded control to the API response
     * @throws JsonProcessingException
     */
    public Mono<ResponseEntity<JsonNode>> apiCall(RouteType routeType, PathBuilder uriBuilderFnc, PathBuilder reviewsSearchUriBuilder, Map<String,String> uriVars, HttpMethod httpMethod) throws ResponseStatusException, JsonProcessingException {
        return this.getWebClient(this.YELP_API_ROOT_URL)
                    .method(httpMethod)
                    .uri(uriBuilderFnc.buildPath(uriVars))
                    .header("Authorization", "Bearer " + API_KEY)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(res -> { return res.toEntity(JsonNode.class);
                        // Mono<ResponseEntity<JsonNode>> successfulFetch = Mono.empty();
                        // if (res.statusCode().equals(HttpStatus.OK)) {
                        //     successfulFetch = res.toEntity(JsonNode.class);
                        // } else if (res.statusCode().is4xxClientError()) {
                        //       try {
                        //         throw new YelpApiResponseException(res.toEntity(JsonNode.class));
                        //       } catch (JsonProcessingException | IllegalArgumentException e) {
                        //         throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
                        //       }
                        // } else {
                        //     try {
                        //         throw new YelpApiResponseException(res.toEntity(JsonNode.class));
                        //     } catch (JsonProcessingException | IllegalArgumentException e) {
                        //         throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
                        //     }
                        // }
                        // return successfulFetch;
                    }).publishOn(Schedulers.single());
                    // .doOnSuccess((res) -> {
                    //     ControllerConfig config = new ControllerConfig();
                    //     if(routeType == RouteType.SEARCH_BY_TERMS) {
                    //         try {
                    //           config.getSearchByTermsRouteResponseBuilder().build(Mono.just(res), this, this.uriVars);
                    //         } catch (ResponseStatusException e) {
                    //           Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT_FOUND", e));
                    //         }
                    //     }
                    //     if(routeType == RouteType.SEARCH_BY_ID) {
                    //         try{
                    //             config.getSearchByIdRouteResponseBuilder().build(Mono.just(res), this, this.uriVars);
                    //         } catch (ResponseStatusException e) {
                    //           Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT_FOUND", e));
                    //         }
                    //     }
                    // }
                    // ,
                    // // // (res) -> { Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("No search results found on this request"))), HttpStatus.NOT_FOUND));
                    // }
                // );
    }

    /**
     * 
     * @param callType
     * @param apiCallResultString
     * @param isBizDetailsSingleton
     * @return Jackson API JsonNode from a Yelp API call result
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
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
    public YelpApi(PathBuilder bizSearchUriBuilder, PathBuilder reviewsSearchUriBuilder, HttpMethod httpMethod, Map<String,String> uriVars) throws JsonMappingException, JsonProcessingException, IllegalArgumentException {
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
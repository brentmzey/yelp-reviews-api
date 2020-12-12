package com.yelpreviews.apiendpoint.apis;

import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearch;
import com.yelpreviews.apiendpoint.exceptions.IncorrectDotEnvFileFormat;
import com.yelpreviews.apiendpoint.utils.DotEnvFileToSysProps;
import com.yelpreviews.apiendpoint.utils.JSON;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class YelpApi {
    public static final String YELP_ROOT_URL = "https://api.yelp.com/v3/businesses"; // THEN: "/{bizId}/reviews"
    private final String YELP_API_ROOT_URL = "https://api.yelp.com/v3/businesses"; // THEN: "/{bizId}/reviews"
    private PathBuilder reviewsSearchUriBuilder;
    private PathBuilder bizSearchUriBuilder;
    private String API_KEY;
    private HttpMethod httpMethod;
    private Map<String,String> uriVars;
    private Mono<String> prefetchBizDetails;
    private boolean isBizDetailsSingleton = false;

    {
        if (System.getProperty("process.env") != null & System.getProperty("process.env") == "prod") {
            API_KEY = System.getenv().get("YELP_API_KEY");
        } else {
            try {
                if(System.getenv().get("USERNAME").equalsIgnoreCase("brent")) {
                    DotEnvFileToSysProps.setCredentialsAsSystemProperties();
                    API_KEY = System.getProperty("YELP_API_KEY");
                }
            } catch (IncorrectDotEnvFileFormat e) {
                System.out.println(e.getMessage());
                System.out.println("Could not get proper Yelp API Key credentials. Program closing.");
                System.exit(0);
            }
        }
    }
    
    public YelpApi(PathBuilder reviewsSearchUriBuilder, PathBuilder bizSearchUriBuilder, HttpMethod httpMethod, Map<String,String> uriVars, boolean isBizSearchCallFirst) throws JsonMappingException, JsonProcessingException, IllegalArgumentException {
        this.reviewsSearchUriBuilder = reviewsSearchUriBuilder;
        this.bizSearchUriBuilder = bizSearchUriBuilder;
        this.httpMethod = httpMethod;
        this.uriVars = uriVars;
        if (isBizSearchCallFirst) {
            // First need to fetch the Yelp Business ID before fetching reviews for that business
            this.prefetchBizDetails = apiCaller(bizSearchUriBuilder, uriVars, httpMethod);
            // Add the business id to the uriVars Map -- only grabbing the first result from that JSON ArrayNode of businesses
            this.uriVars.put("bizId", JSON.jsonToObject(JSON.parseJsonString(prefetchBizDetails.block()), YelpBizSearch.class).getBizId());
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
        this(reviewsSearchUriBuilder, bizSearchUriBuilder, httpMethod, uriVars, false);
        this.isBizDetailsSingleton = true;
    }

    public WebClient getWebClient(String rootUrl) { return WebClient.create(rootUrl); }

    private Mono<String> apiCaller(PathBuilder uriBuilderFnc, Map<String,String> uriVars, HttpMethod httpMethod) {
        return getWebClient(getRootUrl())
                    .method(httpMethod)
                    .uri(uriBuilderFnc.buildPath(uriVars))
                    .header("Authorization", "Bearer " + API_KEY)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(res -> {
                            if (res.statusCode().equals(HttpStatus.OK)) {
                                return res.bodyToMono(String.class);
                            }
                            else if (res.statusCode().is4xxClientError()) {
                                return res.bodyToMono(String.class);
                            }
                            else if (res.statusCode().is5xxServerError()) {
                                return Mono.just("<h3>Issue grabbing a correct response.</h3><h5>Try submitting different search terms.</h5>");
                            }
                            throw res.createException().block();
                        }
                    );
                    // .expectS()
                    // .bodyToMono(String.class);
    }

    /**
     * Getters
     */
    public String getRootUrl() { return this.YELP_API_ROOT_URL; }
    public Mono<String> getReviewsResults() { 
      return apiCaller(this.reviewsSearchUriBuilder, this.uriVars, this.httpMethod);
    };
    public Mono<String> getBizResults() { 
        if(this.prefetchBizDetails != null)
            return this.prefetchBizDetails;
        return apiCaller(this.bizSearchUriBuilder, this.uriVars, this.httpMethod);
    };
    public JsonNode getReviewsJsonArrayNode() throws JsonMappingException, JsonProcessingException { 
        return JSON.parseJsonString(getReviewsResults().block()).get("reviews");
    }
    /**
     * Handles the case if the Yelp business Id was passed in through the URL path and the business details were then fetched as a singleton object from the Yelp API -- rather than when we do a business search by 
     * @return
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public JsonNode getBizDetailsJsonNode() throws JsonMappingException, JsonProcessingException {
        if(isBizDetailsSingleton)
            return JSON.parseJsonString("{ \"businesses\": [" + getBizResults().block() + "]}");
        return JSON.parseJsonString(getBizResults().block());
    }
    
}
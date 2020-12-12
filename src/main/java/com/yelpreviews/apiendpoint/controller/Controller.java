package com.yelpreviews.apiendpoint.controller;

import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yelpreviews.apiendpoint.DTO.ApiResponse;
import com.yelpreviews.apiendpoint.apis.YelpApi;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
public class Controller {
        private boolean needBizIdForReviewsSearch = true;

    /**
     * 
     * @param location REQUIRED!!! city, zip code, etc.  Examples: "New York City", "NYC", "350 5th Ave, New York, NY 10118"
     * @param term REQUIRED!!! specific restaurant name, food category, drink category, etc.
     * @return JSON String of some business info with the top 3 reviews from the Yelp API
     * @throws JsonProcessingException
     * @throws WebClientResponseException
     */
    @GetMapping("/{location}/{term}/reviews")
    Mono<String> reviewsBySearchTerm(@PathVariable String location, @PathVariable String term) throws JsonProcessingException, WebClientResponseException {
        HashMap<String,String> pathVars = new HashMap<String,String>();
        pathVars.put("location", location);
        pathVars.put("term", term);
        
        YelpApi yelpApi = new YelpApi(
            (Map<String,String> uriVariables) -> {
                String path = "/" + uriVariables.get("bizId") + "/reviews";
                return path;
            },
            (Map<String,String> uriVariables) -> { 
                String pathAndQuery = "/search?term=" + uriVariables.get("term") 
                                        + "&location=" + uriVariables.get("location");
                return pathAndQuery;
            },
            HttpMethod.GET,
            pathVars,
            this.needBizIdForReviewsSearch
        );
        ApiResponse apiResponse = new ApiResponse(yelpApi.getReviewsJsonArrayNode(), yelpApi.getBizDetailsJsonNode());
        return Mono.just(apiResponse.toJson());
    }

    @GetMapping("/{bizId}/reviews")
    Mono<String> reviewsByYelpBusinessId(@PathVariable String bizId) throws JsonMappingException, JsonProcessingException, IllegalArgumentException {
        HashMap<String,String> pathVars = new HashMap<String,String>();
        pathVars.put("bizId", bizId);
        
        YelpApi yelpApi = new YelpApi(
            (Map<String,String> uriVariables) -> {
                String path = "/" + uriVariables.get("bizId") + "/reviews";
                return path;
            },
            (Map<String,String> uriVariables) -> { 
                String path = "/" + uriVariables.get("bizId");
                return path;
            },
            HttpMethod.GET,
            pathVars
        );
        ApiResponse apiResponse = new ApiResponse(yelpApi.getReviewsJsonArrayNode(), yelpApi.getBizDetailsJsonNode());
        return Mono.just(apiResponse.toJson());
        
        // Mono<String> bizSearchMono = YelpApi.getWebClient(YelpApi.getYelpApiRootUrl())
        //         .get()
        //         .uri(uriBuilder -> uriBuilder.path("/{bizId}").build(bizId))
        //         .header("Authorization", "Bearer " + API_KEY)
        //         .accept(MediaType.APPLICATION_JSON)
        //         .retrieve()
        //         .bodyToMono(String.class);
        // String bizSearchJson = "{ \"businesses\": [" + bizSearchMono.block() + "]}";

        // Mono<String> reviewsSearchMono = YelpApi.getWebClient(YelpApi.getYelpApiRootUrl())
        //         .get()
        //         .uri(uriBuilder -> uriBuilder.path("/{bizId}/reviews").build(bizId))
        //         .header("Authorization", "Bearer " + API_KEY)
        //         .accept(MediaType.APPLICATION_JSON)
        //         .retrieve()
        //         .bodyToMono(String.class);
        // String reviewsSearchJson = reviewsSearchMono.block();
        // ApiResponse apiResponse = new ApiResponse(JSON.parseJsonString(reviewsSearchJson).get("reviews"), 
        // JSON.parseJsonString(bizSearchJson));
    }

    @RequestMapping("**")
    public String getAnythingelse(){
        return "Request not found";
    }
}
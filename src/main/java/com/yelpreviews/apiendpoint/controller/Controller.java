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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
public class Controller {

    /**
     * 
     * @param term specific restaurant name, food category, drink category, etc.
     * @param location city, zip code, etc.  Examples: "New York City", "NYC", "350 5th Ave, New York, NY 10118"
     * @return JSON String of some business info with the top 3 reviews from the Yelp API
     * @throws JsonProcessingException
     * @throws WebClientResponseException
     */
    @GetMapping("/reviews")
    Mono<String> reviewsBySearchTerm(@RequestParam("term") String term, @RequestParam("location") String location) throws JsonProcessingException, WebClientResponseException {
        HashMap<String,String> pathVars = new HashMap<String,String>();
        pathVars.put("location", location);
        pathVars.put("term", term);
        
        YelpApi yelpApi = new YelpApi(
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId") + "/reviews",
            (Map<String,String> uriVariables) -> { 
                return "/search?term=" + uriVariables.get("term") + "&location=" + uriVariables.get("location");
            },
            HttpMethod.GET,
            pathVars,
            true
        );
        ApiResponse apiResponse = new ApiResponse(
            yelpApi.toJsonNode(YelpApi.CallType.REVIEWS, yelpApi.apiCall(yelpApi.getReviewsSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block(), false), 
            yelpApi.toJsonNode(YelpApi.CallType.BUSINESS, yelpApi.apiCall(yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block(), false)
        );
        return Mono.just(apiResponse.toJson());
    }

    /**
     * 
     * @param bizId
     * @return JSON string 
     * @throws JsonMappingException
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     */
    @GetMapping("/reviews/{bizId}")
    Mono<String> reviewsByYelpBusinessId(@PathVariable String bizId) throws JsonMappingException, JsonProcessingException, IllegalArgumentException {
        HashMap<String,String> pathVars = new HashMap<String,String>();
        pathVars.put("bizId", bizId);
        
        YelpApi yelpApi = new YelpApi(
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId") + "/reviews",
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId"),
            HttpMethod.GET,
            pathVars
        );

        ApiResponse apiResponse = new ApiResponse(
            yelpApi.toJsonNode(YelpApi.CallType.REVIEWS, yelpApi.apiCall(yelpApi.getReviewsSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block(), false), 
            yelpApi.toJsonNode(YelpApi.CallType.BUSINESS, yelpApi.apiCall(yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block(), true)
        );
        return Mono.just(apiResponse.toJson());
    }

    @RequestMapping("*")
    public String getAnythingelse(){
        return "Request not found";
    }
}
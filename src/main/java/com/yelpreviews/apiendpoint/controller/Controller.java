package com.yelpreviews.apiendpoint.controller;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearch;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearchList;
import com.yelpreviews.apiendpoint.DTO.YelpReview;
import com.yelpreviews.apiendpoint.apis.YelpApi;
import com.yelpreviews.apiendpoint.apis.YelpApi.CallType;
import com.yelpreviews.apiendpoint.utils.JSON;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
public class Controller {
    int searchResultsLimit = 10;

    /**
     * 
     * @param term specific restaurant name, food category, drink category, etc.
     * @param location city, zip code, etc.  Examples: "New York City", "NYC", "350 5th Ave, New York, NY 10118"
     * @return JSON String of some business info with the top 3 reviews from the Yelp API
     * @throws JsonProcessingException
     * @throws WebClientResponseException
     */
    @GetMapping("/reviews")
    @ResponseBody
    Mono<String> reviewsBySearchTerm(@RequestParam("term") String term, @RequestParam("location") String location) throws JsonProcessingException, WebClientResponseException {
        HashMap<String,String> pathVars = new HashMap<String,String>();
        pathVars.put("location", location);
        pathVars.put("term", term);
        
        YelpApi yelpApi = new YelpApi(
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId") + "/reviews",
            (Map<String,String> uriVariables) -> { 
                return "/search?term=" + uriVariables.get("term") + "&location=" + uriVariables.get("location");
            },
            HttpMethod.GET, pathVars
        );

        String prefetchedBizArrayString = yelpApi.apiCall(CallType.BUSINESS, yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();
        int count = JSON.parseJsonString(prefetchedBizArrayString).get("total").asInt();
        JsonNode prefetchedBizJsonNode = JSON.parseJsonString(prefetchedBizArrayString).get("businesses");
                    
        List<YelpBizSearch> dataList = new ArrayList<>();

        Iterator<JsonNode> bizJsonArrayNode = prefetchedBizJsonNode.iterator();
        while (bizJsonArrayNode.hasNext() && dataList.size() < searchResultsLimit) {
            
            String bizId = bizJsonArrayNode.next().get("id").asText();
            Map<String, String> uriVarz = yelpApi.getUriVars();
            uriVarz.put("bizId", bizId);
            List<YelpReview> yelpReviews = new ArrayList<YelpReview>();
            Iterator<JsonNode> reviewsArray = JSON.parseJsonString(yelpApi.apiCall(CallType.REVIEWS, yelpApi.getReviewsSearchUriBuilder(), uriVarz, yelpApi.getHttpMethod()).block()).get("reviews").iterator();
            while(reviewsArray.hasNext()) {
                yelpReviews.add(JSON.jsonToObject(reviewsArray.next(), YelpReview.class));
            }
            YelpBizSearch yelpBizSearch = JSON.jsonToObject(bizJsonArrayNode.next(), YelpBizSearch.class);
            yelpBizSearch.setBizReviews(yelpReviews);
            dataList.add(yelpBizSearch);
        }

        YelpBizSearchList yelpBizSearchList = new YelpBizSearchList(count, searchResultsLimit, dataList);

        return Mono.just(JSON.toJson(yelpBizSearchList));
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
    @ResponseBody
    Mono<String> reviewsByYelpBusinessId(@PathVariable String bizId) throws JsonMappingException, JsonProcessingException, IllegalArgumentException {
        HashMap<String,String> pathVars = new HashMap<String,String>();
        pathVars.put("bizId", bizId);
        
        YelpApi yelpApi = new YelpApi(
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId") + "/reviews",
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId"),
            HttpMethod.GET, pathVars
        );

        YelpBizSearch yelpBizSearch = JSON.jsonToObject(yelpApi.apiCall(YelpApi.CallType.BUSINESS, yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block(), YelpBizSearch.class);

        JsonNode reviewsArrayNode = JSON.parseJsonString(yelpApi.apiCall(YelpApi.CallType.REVIEWS, yelpApi.getReviewsSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block());

        List<YelpReview> yelpReviewList = new ArrayList<>();
        Iterator<JsonNode> reviewListIter = reviewsArrayNode.get("reviews").iterator();
            while(reviewListIter.hasNext()){
                yelpReviewList.add(JSON.jsonToObject(reviewListIter.next(), YelpReview.class));
            }
        yelpBizSearch.setBizReviews(yelpReviewList);

        return Mono.just(JSON.toJson(yelpBizSearch));
    }

    @RequestMapping("*")
    @ResponseBody
    public String getAnythingelse(){
        return "Request not found";
    }
}
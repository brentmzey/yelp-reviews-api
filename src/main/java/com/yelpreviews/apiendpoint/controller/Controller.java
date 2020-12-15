package com.yelpreviews.apiendpoint.controller;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.DTO.YelpApiErrors;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearch;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearchList;
import com.yelpreviews.apiendpoint.DTO.YelpReview;
import com.yelpreviews.apiendpoint.apis.YelpApi;
import com.yelpreviews.apiendpoint.apis.YelpApi.CallType;
import com.yelpreviews.apiendpoint.utils.JSON;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class Controller {
    int searchResultsLimit = 10;

    protected String bodyToErrorMsgJsonString(JsonNode jsonNode) throws JsonProcessingException, IllegalArgumentException {
        return JSON.toJson(JSON.jsonToObject(jsonNode, YelpApiErrors.class));
    }

    protected boolean isErrorStatusCode(ResponseEntity<JsonNode> apiResponse) {
        if (!apiResponse.getStatusCode().equals(HttpStatus.OK)) {
            return true;
        }
        return false;
    }


    /**
     * Controller mapping to search Yelp for a a list of business and their respective reviews via query params {term} and {location} -- where term is a food/drink Yelp category and location is a city name, zip code, street address, etc.
     * @param term specific restaurant name, food category, drink category, etc.
     * @param location city, zip code, etc.  Examples: "New York City", "NYC", "350 5th Ave, New York, NY 10118"
     * @return JSON String of some business info with the top 3 reviews from the Yelp API
     * @throws JsonProcessingException
     * @throws WebClientResponseException
     */
    @GetMapping("/reviews")
    ResponseEntity<String> reviewsBySearchTerm(@RequestParam("term") String term, @RequestParam("location") String location) throws JsonProcessingException, WebClientResponseException {
        if (term == null || location == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must supply both a proper Yelp 'term' and 'location' as request query parameters.");
        }
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

        ResponseEntity<JsonNode> bizResponseEntity = yelpApi.apiCall(CallType.BUSINESS, yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();
        
        if (isErrorStatusCode(bizResponseEntity)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                bodyToErrorMsgJsonString(bizResponseEntity.getBody()));
        }

        JsonNode prefetchedBizJsonObject = bizResponseEntity.getBody();
        int count = prefetchedBizJsonObject.get("total").asInt();
        List<Map<String,?>> prefetchedBizList = (ArrayList<Map<String,?>>)JSON.jsonToObject(prefetchedBizJsonObject.get("businesses"), ArrayList.class);
                    
        List<YelpBizSearch> dataList = new ArrayList<>();
        System.out.println("Json Object: " + prefetchedBizJsonObject);
        System.out.println("Array Object: " + prefetchedBizList);
        for (Map<String,?> business : prefetchedBizList.subList(0, prefetchedBizList.size() <= searchResultsLimit ? prefetchedBizList.size() : searchResultsLimit)) {
            Map<String, String> uriVarz = yelpApi.getUriVars();
            uriVarz.put("bizId", (String)business.get("id"));

            ResponseEntity<JsonNode> reviewsResponseEntity = yelpApi.apiCall(CallType.REVIEWS, yelpApi.getReviewsSearchUriBuilder(), uriVarz, yelpApi.getHttpMethod()).block();
            if (isErrorStatusCode(reviewsResponseEntity)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                bodyToErrorMsgJsonString(reviewsResponseEntity.getBody()));
            }

            List<YelpReview> yelpReviews = new ArrayList<YelpReview>();
            Iterator<JsonNode> reviewsArray = reviewsResponseEntity.getBody().get("reviews").iterator();
            while(reviewsArray.hasNext()) {
                yelpReviews.add(JSON.jsonToObject(reviewsArray.next(), YelpReview.class));
            }
            YelpBizSearch yelpBizSearch = JSON.jsonToObject(JSON.objectToJsonNode(business), YelpBizSearch.class);
            yelpBizSearch.setBizReviews(yelpReviews);
            dataList.add(yelpBizSearch);
        }

        YelpBizSearchList yelpBizSearchList = new YelpBizSearchList(count, searchResultsLimit, dataList);

        return new ResponseEntity<String>(JSON.toJson(yelpBizSearchList), HttpStatus.OK);
    }

    /**
     * Controller mapping to search Yelp for a single business and its reviews via that particular business' Yelp ID
     * @param bizId
     * @return JSON string 
     * @throws JsonMappingException
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     */
    @GetMapping("/reviews/{bizId}")
    ResponseEntity<String> reviewsByYelpBusinessId(@PathVariable String bizId) throws JsonMappingException, JsonProcessingException, IllegalArgumentException {
        HashMap<String,String> pathVars = new HashMap<String,String>();
        pathVars.put("bizId", bizId);
        
        YelpApi yelpApi = new YelpApi(
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId") + "/reviews",
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId"),
            HttpMethod.GET, pathVars
        );

        ResponseEntity<JsonNode> bizResponseEntity = yelpApi.apiCall(YelpApi.CallType.BUSINESS, yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();
        ResponseEntity<JsonNode> reviewsResponseEntity = yelpApi.apiCall(YelpApi.CallType.REVIEWS, yelpApi.getReviewsSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();
        
        if (isErrorStatusCode(bizResponseEntity)) { 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bodyToErrorMsgJsonString(bizResponseEntity.getBody()));
        } else if (isErrorStatusCode(reviewsResponseEntity)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bodyToErrorMsgJsonString(reviewsResponseEntity.getBody()));
        }

        YelpBizSearch yelpBizSearch = JSON.jsonToObject(bizResponseEntity.getBody(), YelpBizSearch.class);

        List<YelpReview> yelpReviewList = new ArrayList<>();
        Iterator<JsonNode> reviewListIter = reviewsResponseEntity.getBody().get("reviews").iterator();
            while (reviewListIter.hasNext()) {
                yelpReviewList.add(JSON.jsonToObject(reviewListIter.next(), YelpReview.class));
            }
        yelpBizSearch.setBizReviews(yelpReviewList);

        return new ResponseEntity<String>(JSON.toJson(yelpBizSearch), HttpStatus.OK);
    }

    @RequestMapping("*")
    @ResponseBody
    public String getAnythingelse(){
        return "Request not found";
    }

}
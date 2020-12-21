package com.yelpreviews.apiendpoint.controller;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.DTO.YelpApiError;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearch;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearchList;
import com.yelpreviews.apiendpoint.DTO.YelpReview;
import com.yelpreviews.apiendpoint.apis.YelpApi;
import com.yelpreviews.apiendpoint.apis.YelpApi.CallType;
import com.yelpreviews.apiendpoint.exceptions.InvalidRequestParametersException;
import com.yelpreviews.apiendpoint.exceptions.PathNotFoundException;
import com.yelpreviews.apiendpoint.exceptions.YelpApiResponseException;
import com.yelpreviews.apiendpoint.utils.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerErrorException;

@RestController
@ComponentScan(basePackageClasses = com.yelpreviews.apiendpoint.controller.ControllerConfig.class)
public class Controller {

    public enum RouteType { SEARCH_BY_TERMS, SEARCH_BY_ID };
    @Autowired
    private ControllerConfig config;


    /**
     * Controller mapping to search Yelp for a a list of business and their respective reviews via query params {term} and {location} -- where term is a food/drink Yelp category and location is a city name, zip code, street address, etc.
     * @param term specific restaurant name, food category, drink category, etc.
     * @param location city, zip code, etc.  Examples: "New York City", "NYC", "350 5th Ave, New York, NY 10118"
     * @return JSON String of some business info with the top 3 reviews from the Yelp API
     * @throws JsonProcessingException
     * @throws WebClientResponseException
     * @throws InvalidRequestParametersException
     */
    @GetMapping(value = "/reviews", produces = "application/json")
    @Validated
    ResponseEntity<Object> reviewsBySearchTerm(@RequestParam("term") String term, @RequestParam(value = "location", required = true) String location) throws JsonProcessingException, WebClientResponseException, InvalidRequestParametersException {
        if (term == null || term == "" || term.trim() == "") {
            throw new InvalidRequestParametersException(HttpStatus.BAD_REQUEST, "term", "String", term);
        }
        if (location == null || location == "" || location.trim() == "") {
            throw new InvalidRequestParametersException(HttpStatus.BAD_REQUEST, "location", "String", location);
        }
        HashMap<String,String> pathVars = new HashMap<String,String>();
        pathVars.put("location", location);
        pathVars.put("term", term);
        
        YelpApi yelpApi = new YelpApi(
            (Map<String,String> uriVariables) -> { 
                return "/search?term=" + uriVariables.get("term") + "&location=" + uriVariables.get("location");
            },
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId") + "/reviews",
            HttpMethod.GET, pathVars
        );

        ResponseEntity<JsonNode> bizResponseEntity = yelpApi.apiCall(CallType.BUSINESS, yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();

        JsonNode prefetchedBizJsonObject = bizResponseEntity.getBody();
        int count = prefetchedBizJsonObject.get("total").asInt();
        List<Map<String,?>> prefetchedBizList = (ArrayList<Map<String,?>>)JSON.jsonToObject(prefetchedBizJsonObject.get("businesses"), ArrayList.class);
                    
        List<YelpBizSearch> dataList = new ArrayList<>();
        for (Map<String,?> business : prefetchedBizList.subList(0, prefetchedBizList.size() < config.getSearchResultsLimit() ? prefetchedBizList.size() : config.getSearchResultsLimit())) {
            Map<String, String> uriVarz = yelpApi.getUriVars();
            uriVarz.put("bizId", (String)business.get("id"));

            ResponseEntity<JsonNode> reviewsResponseEntity = yelpApi.apiCall(CallType.REVIEWS, yelpApi.getReviewsSearchUriBuilder(), uriVarz, yelpApi.getHttpMethod()).block();

            List<YelpReview> yelpReviews = new ArrayList<YelpReview>();
            Iterator<JsonNode> reviewsArray = reviewsResponseEntity.getBody().get("reviews").iterator();
            while(reviewsArray.hasNext()) {
                yelpReviews.add(JSON.jsonToObject(reviewsArray.next(), YelpReview.class));
            }
            YelpBizSearch yelpBizSearch = JSON.jsonToObject(JSON.objectToJsonNode(business), YelpBizSearch.class);
            yelpBizSearch.setBizReviews(yelpReviews);
            dataList.add(yelpBizSearch);
        }
        
        YelpBizSearchList yelpBizSearchList = new YelpBizSearchList(count, config.getSearchResultsLimit(), dataList);
        return new ResponseEntity<Object>(JSON.toJson(yelpBizSearchList), HttpStatus.OK);
    }

    /**
     * Controller mapping to search Yelp for a single business and its reviews via that particular business' Yelp ID
     * @param bizId
     * @return JSON string 
     * @throws JsonMappingException
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     */
    @GetMapping(value = "/reviews/{bizId}", produces = "application/json")
    @Validated
    ResponseEntity<Object> reviewsByYelpBusinessId(@PathVariable String bizId) throws JsonMappingException, JsonProcessingException, IllegalArgumentException {
        HashMap<String,String> pathVars = new HashMap<String,String>();
        pathVars.put("bizId", bizId);
        
        YelpApi yelpApi = new YelpApi(
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId"),
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId") + "/reviews",
            HttpMethod.GET, pathVars
        );

        ResponseEntity<JsonNode> bizResponseEntity = yelpApi.apiCall(YelpApi.CallType.BUSINESS, yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();
        ResponseEntity<JsonNode> reviewsResponseEntity = yelpApi.apiCall(YelpApi.CallType.REVIEWS, yelpApi.getReviewsSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();

        if (bizResponseEntity.getStatusCode().is4xxClientError() || reviewsResponseEntity.getStatusCode().is4xxClientError()) {
            JsonNode bodyNode = bizResponseEntity.getBody();
            System.out.println(bodyNode);
            try {
                throw new YelpApiResponseException(bizResponseEntity.getStatusCode(), JSON.jsonToObject(bodyNode, YelpApiError.class));
            } catch (JsonProcessingException | IllegalArgumentException e) {
                throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
            }
        }
  
        YelpBizSearch yelpBizSearch = JSON.jsonToObject(bizResponseEntity.getBody(), YelpBizSearch.class);

        List<YelpReview> yelpReviewList = new ArrayList<>();
        Iterator<JsonNode> reviewListIter = reviewsResponseEntity.getBody().get("reviews").iterator();
            while (reviewListIter.hasNext()) {
                yelpReviewList.add(JSON.jsonToObject(reviewListIter.next(), YelpReview.class));
            }
        yelpBizSearch.setBizReviews(yelpReviewList);

        return new ResponseEntity<Object>(JSON.toJson(yelpBizSearch), HttpStatus.OK);
    }

    @RequestMapping("*")
    public String getAnythingelse(){
        throw new PathNotFoundException(HttpStatus.NOT_FOUND, "The requested path does not exist.");
    }

}
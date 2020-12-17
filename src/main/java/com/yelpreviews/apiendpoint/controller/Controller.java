package com.yelpreviews.apiendpoint.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.exceptions.InvalidRequestParametersException;
import com.yelpreviews.apiendpoint.exceptions.PathNotFoundException;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearch;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearchList;
import com.yelpreviews.apiendpoint.DTO.YelpReview;
import com.yelpreviews.apiendpoint.apis.YelpApi;
import com.yelpreviews.apiendpoint.apis.YelpApi.CallType;
import com.yelpreviews.apiendpoint.utils.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestController
@ComponentScan(basePackages = "com.yelpreviews.apiendpoint.controller")
public class Controller {
    // private ControllerRouteResponseHandlers routeResponseHandlers = new ControllerRouteResponseHandlers(Map.of("termAndLocationSearchRoute", v1, "businessIdSearchRoute", v2));
    // private int searchResultsLimit = 10;

    @Autowired
    private ControllerInit controllerInit;

    /**
     * Controller mapping to search Yelp for a a list of business and their respective reviews via query params {term} and {location} -- where term is a food/drink Yelp category and location is a city name, zip code, street address, etc.
     * @param term specific restaurant name, food category, drink category, etc.
     * @param location city, zip code, etc.  Examples: "New York City", "NYC", "350 5th Ave, New York, NY 10118"
     * @return JSON String of some business info with the top 3 reviews from the Yelp API
     * @throws JsonProcessingException
     * @throws WebClientResponseException
     * @throws MissingServletRequestPartException
     * @throws MissingServletRequestParameterException
     */
    @GetMapping(value = "/reviews", produces = "application/json")
    @Validated
    ResponseEntity<Object> reviewsBySearchTerm(@RequestParam("term") String term, @RequestParam(value = "location", required = true) String location) throws JsonProcessingException, InvalidRequestParametersException {
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
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId") + "/reviews",
            (Map<String,String> uriVariables) -> { 
                return "/search?term=" + uriVariables.get("term") + "&location=" + uriVariables.get("location");
            },
            HttpMethod.GET, pathVars
        );

        ResponseBodyBuilder bodyBuilder = controllerInit.getRouteResponseHandlers().getRouteResponseHandlers().get("termAndLocationSearchRoute");

        return bodyBuilder.build(yelpApi.apiCall(CallType.BUSINESS, yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block(), yelpApi.apiCall(CallType.REVIEWS, yelpApi.getReviewsSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block(), pathVars);
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
    ResponseEntity<Object> reviewsByYelpBusinessId(@PathVariable @NotNull @NotBlank String bizId) throws JsonMappingException, JsonProcessingException, IllegalArgumentException {
        HashMap<String,String> pathVars = new HashMap<String,String>();
        pathVars.put("bizId", bizId);
        
        YelpApi yelpApi = new YelpApi(
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId") + "/reviews",
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId"),
            HttpMethod.GET, pathVars
        );

        // ResponseEntity<JsonNode> bizResponseEntity = yelpApi.apiCall(YelpApi.CallType.BUSINESS, yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();
        // ResponseEntity<JsonNode> reviewsResponseEntity = yelpApi.apiCall(YelpApi.CallType.REVIEWS, yelpApi.getReviewsSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();

        ResponseBodyBuilder bodyBuilder = controllerInit.getRouteResponseHandlers().getRouteResponseHandlers().get("businessIdSearchRoute");

        return bodyBuilder.build(yelpApi.apiCall(CallType.BUSINESS, yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block(), yelpApi.apiCall(CallType.REVIEWS, yelpApi.getReviewsSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block(), pathVars);    
    }

    @RequestMapping("*")
    public ResponseEntity<Object> getAnythingElse(HttpServletRequest request) {
        throw new PathNotFoundException(HttpStatus.NOT_FOUND, "A request to the specified path is not allowed.");
    }

}
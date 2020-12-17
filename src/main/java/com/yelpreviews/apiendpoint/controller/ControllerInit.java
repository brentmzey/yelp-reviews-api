package com.yelpreviews.apiendpoint.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearch;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearchList;
import com.yelpreviews.apiendpoint.DTO.YelpReview;
import com.yelpreviews.apiendpoint.utils.JSON;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerErrorException;

@Component
public class ControllerInit implements InitializingBean {

    private int searchResultsLimit = 10;
    private ControllerRouteResponseHandlers routeResponseHandlers;

    // public ControllerInit(ControllerRouteResponseHandlers routeResponseHandlers, int searchResultsLimit) {
    //     this.searchResultsLimit = searchResultsLimit;
    //     this.routeResponseHandlers = routeResponseHandlers;
    // }
    // public ControllerInit(int searchResultsLimit) { 
    //     this(
    //  }

     public ControllerInit() {
         this.routeResponseHandlers = new ControllerRouteResponseHandlers(Map.of("termAndLocationSearchRoute", (ResponseEntity<JsonNode> bizResponseEntity, ResponseEntity<JsonNode> reviewsResponseEntity, Map<String,String> uriVars) -> {
            // ResponseEntity<JsonNode> bizResponseEntity = yelpApi.apiCall(CallType.BUSINESS, yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();

            JsonNode prefetchedBizJsonObject = bizResponseEntity.getBody();
            int count = prefetchedBizJsonObject.get("total").asInt();
            List<Map<String, ?>> prefetchedBizList;
            try {
              prefetchedBizList = (ArrayList<Map<String,?>>)JSON.jsonToObject(prefetchedBizJsonObject.get("businesses"), ArrayList.class);
            } catch (JsonProcessingException | IllegalArgumentException e1) {
              throw new ServerErrorException("INTERNAL_SERVER_ERROR", e1);
            }
                        
            List<YelpBizSearch> dataList = new ArrayList<>();
            for (Map<String,?> business : prefetchedBizList.subList(0, prefetchedBizList.size() <= searchResultsLimit ? prefetchedBizList.size() : searchResultsLimit)) {
                Map<String, String> uriVarz = uriVars;
                uriVarz.put("bizId", (String)business.get("id"));

                // ResponseEntity<JsonNode> reviewsResponseEntity = yelpApi.apiCall(CallType.REVIEWS, yelpApi.getReviewsSearchUriBuilder(), uriVarz, yelpApi.getHttpMethod()).block();

                List<YelpReview> yelpReviews = new ArrayList<YelpReview>();
                Iterator<JsonNode> reviewsArray = reviewsResponseEntity.getBody().get("reviews").iterator();
                while(reviewsArray.hasNext()) {
                    try {
                      yelpReviews.add(JSON.jsonToObject(reviewsArray.next(), YelpReview.class));
                    } catch (JsonProcessingException | IllegalArgumentException e) {
                      throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
                    }
                }
                YelpBizSearch yelpBizSearch;
                try {
                  yelpBizSearch = JSON.jsonToObject(JSON.objectToJsonNode(business), YelpBizSearch.class);
                } catch (JsonProcessingException | IllegalArgumentException e) {
                  throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
                }
                yelpBizSearch.setBizReviews(yelpReviews);
                dataList.add(yelpBizSearch);
            }

            YelpBizSearchList yelpBizSearchList = new YelpBizSearchList(count, searchResultsLimit, dataList);

            try {
              return new ResponseEntity<Object>(JSON.toJson(yelpBizSearchList), HttpStatus.OK);
            } catch (JsonProcessingException e) {
              throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
            }
            },
            "businessIdSearchRoute", (ResponseEntity<JsonNode> bizResponseEntity, ResponseEntity<JsonNode> reviewsResponseEntity, Map<String,String> uriVars) -> { 
                //  ResponseEntity<JsonNode> bizResponseEntity = yelpApi.apiCall(YelpApi.CallType.BUSINESS, yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();
                // ResponseEntity<JsonNode> reviewsResponseEntity = yelpApi.apiCall(YelpApi.CallType.REVIEWS, yelpApi.getReviewsSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();

            YelpBizSearch yelpBizSearch;
            try {
              yelpBizSearch = JSON.jsonToObject(bizResponseEntity.getBody(), YelpBizSearch.class);
            } catch (JsonProcessingException | IllegalArgumentException e) {
              throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
            }

            List<YelpReview> yelpReviewList = new ArrayList<>(0);
            if(reviewsResponseEntity.getBody().get("reviews").isArray()) {
                Iterator<JsonNode> reviewListIter = reviewsResponseEntity.getBody().get("reviews").iterator();
                    while (reviewListIter.hasNext()) {
                        try {
                          yelpReviewList.add(JSON.jsonToObject(reviewListIter.next(), YelpReview.class));
                        } catch (JsonProcessingException | IllegalArgumentException e) {
                          throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
                        }
                    }
                yelpBizSearch.setBizReviews(yelpReviewList);
            }

            try {
              return new ResponseEntity<Object>(JSON.toJson(yelpBizSearch), HttpStatus.OK);
            } catch (JsonProcessingException e) {
              throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
            }
            } 
        )
     );
     this.searchResultsLimit = 10;
     }
    
    @Override
    public void afterPropertiesSet() throws Exception {}
    
    // public void afterPropertiesSet(ControllerRouteResponseHandlers routeResponseHandlers, int searchResultsLimit) throws Exception {
    //     afterPropertiesSet();
    //     this.routeResponseHandlers = routeResponseHandlers; 
    //     this.searchResultsLimit = searchResultsLimit;
    // }

    public int getSearchResultsLimit() {
      return searchResultsLimit;
    }
    public ControllerRouteResponseHandlers getRouteResponseHandlers() {
      return routeResponseHandlers;
    }
    public void setSearchResultsLimit(int searchResultsLimit) {
        this.searchResultsLimit = searchResultsLimit;
    }
    public void setRouteResponseHandlers(ControllerRouteResponseHandlers routeResponseHandlers) {
    this.routeResponseHandlers = routeResponseHandlers;
    }
    
}
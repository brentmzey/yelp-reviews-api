package com.yelpreviews.apiendpoint.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearch;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearchList;
import com.yelpreviews.apiendpoint.DTO.YelpReview;
import com.yelpreviews.apiendpoint.apis.YelpApi;
import com.yelpreviews.apiendpoint.apis.YelpApi.CallType;
import com.yelpreviews.apiendpoint.utils.JSON;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerErrorException;

@Component
public class ControllerConfig implements InitializingBean, DisposableBean {

  private int searchResultsLimit = 10;
  private ResponseBodyBuilder searchByTermsRouteResponseBuilder;
  private ResponseBodyBuilder searchByIdRouteResponseBuilder;

  public ControllerConfig(ResponseBodyBuilder searchByTermsRouteResponseBuilder, ResponseBodyBuilder searchByIdRouteResponseBuilder, int searchResultsLimit) {
      this.searchResultsLimit = searchResultsLimit;
      this.searchByTermsRouteResponseBuilder = searchByTermsRouteResponseBuilder;
      this.searchByIdRouteResponseBuilder = searchByIdRouteResponseBuilder;
  }
  public ControllerConfig(int searchResultsLimit) { 
    this(
      (YelpApi yelpApi, Map<String,String> uriVars) -> {
        try {
        ResponseEntity<JsonNode> bizResponseEntity = yelpApi.apiCall(CallType.BUSINESS, yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();
        
        // if (isErrorStatusCode(bizResponseEntity)) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
        //         bodyToErrorMsgJsonString(bizResponseEntity.getBody()));
        // }

        JsonNode prefetchedBizJsonObject = bizResponseEntity.getBody();
        int count = prefetchedBizJsonObject.get("total").asInt();
        List<Map<String,?>> prefetchedBizList = (ArrayList<Map<String,?>>)JSON.jsonToObject(prefetchedBizJsonObject.get("businesses"), ArrayList.class);
                    
        List<YelpBizSearch> dataList = new ArrayList<>();
        for (Map<String,?> business : prefetchedBizList.subList(0, prefetchedBizList.size() < searchResultsLimit ? prefetchedBizList.size() : searchResultsLimit)) {
            Map<String, String> uriVarz = yelpApi.getUriVars();
            uriVarz.put("bizId", (String)business.get("id"));

            ResponseEntity<JsonNode> reviewsResponseEntity = yelpApi.apiCall(CallType.REVIEWS, yelpApi.getReviewsSearchUriBuilder(), uriVarz, yelpApi.getHttpMethod()).block();
            // if (isErrorStatusCode(reviewsResponseEntity)) {
            //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
            //     bodyToErrorMsgJsonString(reviewsResponseEntity.getBody()));
            // }

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
        return new ResponseEntity<Object>(JSON.toJson(yelpBizSearchList), HttpStatus.OK);
      } catch (JsonProcessingException e) {
        throw new ServerErrorException("INTERNAL_SERVER_ERROR");
      }
      }, 
      (YelpApi yelpApi, Map<String,String> uriVars) -> {
        try {
          ResponseEntity<JsonNode> bizResponseEntity = yelpApi.apiCall(YelpApi.CallType.BUSINESS, yelpApi.getBizSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();
          ResponseEntity<JsonNode> reviewsResponseEntity = yelpApi.apiCall(YelpApi.CallType.REVIEWS, yelpApi.getReviewsSearchUriBuilder(), yelpApi.getUriVars(), yelpApi.getHttpMethod()).block();
          
          // if (isErrorStatusCode(bizResponseEntity)) { 
          //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bodyToErrorMsgJsonString(bizResponseEntity.getBody()));
          // } else if (isErrorStatusCode(reviewsResponseEntity)) {
          //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bodyToErrorMsgJsonString(reviewsResponseEntity.getBody()));
          // }
  
          YelpBizSearch yelpBizSearch = JSON.jsonToObject(bizResponseEntity.getBody(), YelpBizSearch.class);
  
          List<YelpReview> yelpReviewList = new ArrayList<>();
          Iterator<JsonNode> reviewListIter = reviewsResponseEntity.getBody().get("reviews").iterator();
              while (reviewListIter.hasNext()) {
                  yelpReviewList.add(JSON.jsonToObject(reviewListIter.next(), YelpReview.class));
              }
          yelpBizSearch.setBizReviews(yelpReviewList);
  
          return new ResponseEntity<Object>(JSON.toJson(yelpBizSearch), HttpStatus.OK);
        } catch (JsonProcessingException e) {
          throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
        }
      },
        searchResultsLimit);
    }

    public ControllerConfig() { this(10); }
    
    @Override
    public void afterPropertiesSet() throws Exception {
      System.out.println("Initialized the ControllerConfig bean inside of the controller.");
    }

    @Override
    public void destroy() throws Exception {
      System.out.println("Destroyed ControllerConfig bean after Controller class popped off of the stack. Clean up complete.");
    }

    public int getSearchResultsLimit() {
      return searchResultsLimit;
    }
    public ResponseBodyBuilder getSearchByTermsRouteResponseBuilder() {
      return this.searchByTermsRouteResponseBuilder;
    }
    public ResponseBodyBuilder getSearchByIdRouteResponseBuilder() {
      return this.searchByIdRouteResponseBuilder;
    }
    public void setSearchResultsLimit(int searchResultsLimit) {
        this.searchResultsLimit = searchResultsLimit;
    }
    public void setSearchByTermsRouteResponseBuilder(ResponseBodyBuilder searchByTermsRouteResponseBuilder) {
      this.searchByTermsRouteResponseBuilder = searchByTermsRouteResponseBuilder;
    }
    public void setSearchByIdRouteResponseBuilder(ResponseBodyBuilder searchByIdRouteResponseBuilder) {
      this.searchByIdRouteResponseBuilder = searchByIdRouteResponseBuilder;
    }
    
}
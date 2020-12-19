package com.yelpreviews.apiendpoint.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.DTO.ApiError;
import com.yelpreviews.apiendpoint.DTO.WrapperApiError;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearch;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearchList;
import com.yelpreviews.apiendpoint.DTO.YelpReview;
import com.yelpreviews.apiendpoint.apis.YelpApi;
import com.yelpreviews.apiendpoint.controller.Controller.RouteType;
import com.yelpreviews.apiendpoint.utils.JSON;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

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
        Mono<ResponseEntity<Object>> response = Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("term is missing or null in the request", "term", uriVars.get("term")))), HttpStatus.NOT_FOUND));
        try {
          // Mono<ResponseEntity<JsonNode>> bizMono = yelpApi.apiCall(RouteType.SEARCH_BY_TERMS, yelpApi.getBizSearchUriBuilder(), yelpApi.getReviewsSearchUriBuilder(), uriVars, yelpApi.getHttpMethod());
          response = yelpApi.apiCall(RouteType.SEARCH_BY_TERMS, yelpApi.getBizSearchUriBuilder(), yelpApi.getReviewsSearchUriBuilder(), uriVars, yelpApi.getHttpMethod()).flatMap((ResponseEntity<JsonNode> bizResponseEntity) -> {
          Mono<ResponseEntity<Object>> mappedResponse = Mono.empty();
          try {
            List<YelpBizSearch> dataList = new ArrayList<>();
            Map<String,Mono<YelpBizSearch>> bizResultMonos = new LinkedHashMap<String,Mono<YelpBizSearch>>();

            List<Map<String, ?>> prefetchedBizList = (ArrayList<Map<String,?>>)JSON.jsonToObject(bizResponseEntity.getBody().get("businesses"), ArrayList.class);
  
            List<Map<String,?>> searchLimitSublist = prefetchedBizList.subList(0, prefetchedBizList.size() <= searchResultsLimit ? prefetchedBizList.size()+1 : searchResultsLimit);
  
            for (Map<String,?> business : searchLimitSublist) {
              bizResultMonos.put((String)business.get("id"), Mono.just(JSON.jsonToObject(JSON.objectToJsonNode(business), YelpBizSearch.class)));
              System.out.println(JSON.toJson(business));
            }

            for (String id : bizResultMonos.keySet()) {
              Map<String,String> uriVarz = yelpApi.getUriVars();
              System.out.println(id);
              uriVarz.put("bizId", id);
              System.out.println(uriVarz);

              Mono<ResponseEntity<JsonNode>> fetchReviewMono = yelpApi.apiCall(RouteType.SEARCH_BY_TERMS, yelpApi.getReviewsSearchUriBuilder(), yelpApi.getReviewsSearchUriBuilder(), uriVarz, yelpApi.getHttpMethod());

              Mono<YelpBizSearch> yelpBizSearchMono = bizResultMonos.get(id).zipWith(fetchReviewMono).map(tuple -> {
                  YelpBizSearch yelpBizSearch = tuple.getT1();
                  ResponseEntity<JsonNode> reviewsNode = tuple.getT2();
                  
                  List<YelpReview> yelpReviews = new ArrayList<YelpReview>();
                  Iterator<JsonNode> reviewsArray = reviewsNode.getBody().get("reviews").iterator();
                  while(reviewsArray.hasNext()) {
                    try {
                      yelpReviews.add(JSON.jsonToObject(reviewsArray.next(), YelpReview.class));
                    } catch (JsonProcessingException | IllegalArgumentException e) {
                      System.out.println(e.getMessage());
                    }
                  }
                  // YelpBizSearch yelpBizSearch = new YelpBizSearch();
                  // try {
                  //   yelpBizSearch = JSON.jsonToObject(JSON.objectToJsonNode(bizMap), YelpBizSearch.class);
                  // } catch (JsonProcessingException | IllegalArgumentException e) {
                  //   System.out.println(e.getMessage());
                  // }
                  yelpBizSearch.setBizReviews(yelpReviews);
                  return yelpBizSearch;
                }
              );
              yelpBizSearchMono.doOnNext(yelpBizSearch -> dataList.add(yelpBizSearch)).subscribe();
            }
            YelpBizSearchList yelpBizSearchList = new YelpBizSearchList((int)bizResponseEntity.getBody().get("total").asInt(), searchResultsLimit, dataList);
            mappedResponse = Mono.just(new ResponseEntity<Object>(yelpBizSearchList, HttpStatus.OK));
        } catch (JsonProcessingException e) {
          if(uriVars.get("term") == null){
              return Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("term is missing or null in the request", "term", uriVars.get("term")))), HttpStatus.NOT_FOUND));
          }
          if(uriVars.get("location") == null){
              return Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("location is missing or null in the request", "location", uriVars.get("location")))), HttpStatus.NOT_FOUND));
          }
        }
        return mappedResponse;
      });
      } catch (JsonProcessingException e) {
          if(uriVars.get("term") == null){
              return Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("term is missing or null in the request", "term", uriVars.get("term")))), HttpStatus.NOT_FOUND));
          }
          if(uriVars.get("location") == null){
              return Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("location is missing or null in the request", "location", uriVars.get("location")))), HttpStatus.NOT_FOUND));
          }
        }
      return response;
          // int count = bizResponseEntity.getBody().get("total").asInt();
          // List<Map<String, ?>> prefetchedBizList = (ArrayList<Map<String,?>>)JSON.jsonToObject(bizResponseEntity.getBody().get("businesses"), ArrayList.class);

          // return Mono.just(prefetchedBizList.subList(0, prefetchedBizList.size() <= searchResultsLimit ? prefetchedBizList.size() : searchResultsLimit).forEach(business -> Mono.just(business)));

          //     List<YelpBizSearch> dataList = new ArrayList<>();
          //     for (Mono<Map<String,?>> bizMono : prefetchedBizMonos) {
          //       Map<String, String> uriVarz = uriVars;
          //       bizMono.subscribe(business -> uriVarz.put("bizId", (String)business.get("id")));
        
          //       Mono<ResponseEntity<JsonNode>> singleBizReviewsMono = yelpApi.apiCall(CallType.REVIEWS, yelpApi.getReviewsSearchUriBuilder(), pathVars, yelpApi.getHttpMethod());

          //       singleBizReviewsMono.subsc
          //       List<YelpReview> yelpReviews = new ArrayList<YelpReview>();
          //       Iterator<JsonNode> reviewsArray = reviewsResponseEntity.getBody().get("reviews").iterator();
          //       while(reviewsArray.hasNext()) {
          //         yelpReviews.add(JSON.jsonToObject(reviewsArray.next(), YelpReview.class));
          //       }
          //       YelpBizSearch yelpBizSearch;
          //         yelpBizSearch = JSON.jsonToObject(JSON.objectToJsonNode(business), YelpBizSearch.class);
          //       yelpBizSearch.setBizReviews(yelpReviews);
          //       dataList.add(yelpBizSearch);
          //     }
      
          //   YelpBizSearchList yelpBizSearchList = new YelpBizSearchList(count, searchResultsLimit, dataList);
    
          //   response = Mono.just(new ResponseEntity<Object>(JSON.toJson(yelpBizSearchList), HttpStatus.OK));
          //   // } catch (JsonProcessingException e) {
          //   //   throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
          //   // }
          //   return response;
            // else if (res.statusCode().is4xxClientError()) {
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
      }, 
      (YelpApi yelpApi, Map<String,String> uriVars) -> {
          Mono<ResponseEntity<JsonNode>> reviewsMono;
          Mono<ResponseEntity<Object>> response = Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("Could not find results for that Yelp business ID.", "businessId", uriVars.get("bizId")))), HttpStatus.NOT_FOUND));
          try {
            Mono<ResponseEntity<JsonNode>> bizMono = yelpApi.apiCall(RouteType.SEARCH_BY_TERMS, yelpApi.getBizSearchUriBuilder(), yelpApi.getReviewsSearchUriBuilder(), uriVars, yelpApi.getHttpMethod());
            reviewsMono = yelpApi.apiCall(RouteType.SEARCH_BY_ID, yelpApi.getReviewsSearchUriBuilder(), yelpApi.getReviewsSearchUriBuilder(), uriVars, yelpApi.getHttpMethod());

          response = bizMono.zipWith(reviewsMono).flatMap(tuple -> {
              ResponseEntity<JsonNode> bizResEntity = tuple.getT1();
              ResponseEntity<JsonNode> reviewsResEntity = tuple.getT2();
              Mono<ResponseEntity<Object>> bizSearch;
              if (bizResEntity.getStatusCode() != HttpStatus.OK || reviewsResEntity.getStatusCode() != HttpStatus.OK) {
                return Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("Could not find results for that Yelp business ID.", "businessId", uriVars.get("bizId")))), HttpStatus.NOT_FOUND));
              }
              try {
                JsonNode reviewsResponseNode = reviewsResEntity.getBody();
                YelpBizSearch yelpBizSearch = JSON.jsonToObject(bizResEntity.getBody(), YelpBizSearch.class);

                List<YelpReview> yelpReviewList = new ArrayList<>(0);
                if(reviewsResponseNode.get("reviews").isArray()) {
                  Iterator<JsonNode> reviewListIter = reviewsResponseNode.get("reviews").iterator();
                      while (reviewListIter.hasNext()) {
                        yelpReviewList.add(JSON.jsonToObject(reviewListIter.next(), YelpReview.class));
                      }
                  yelpBizSearch.setBizReviews(yelpReviewList);
                }
                bizSearch = Mono.just(new ResponseEntity<Object>(JSON.toJson(yelpBizSearch), HttpStatus.OK));
                return bizSearch;
              } catch (JsonProcessingException e) {
                return Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("Could not find results for that Yelp business ID.", "businessId", uriVars.get("bizId")))), HttpStatus.NOT_FOUND));
              }
            }
          );
          } catch (ResponseStatusException | JsonProcessingException e1) {
            System.out.println(e1.getMessage());
          }
          return response;
        },
        // } catch (JsonProcessingException | IllegalArgumentException e) {
        //   throw new ServerErrorException("INTERNAL_SERVER_ERROR", e);
        // }
 
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
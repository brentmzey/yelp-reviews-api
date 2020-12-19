package com.yelpreviews.apiendpoint.controller;

import java.util.Map;
import java.util.concurrent.Flow.Subscriber;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.exceptions.InvalidRequestParametersException;
import com.yelpreviews.apiendpoint.exceptions.PathNotFoundException;
import com.yelpreviews.apiendpoint.utils.JSON;
import com.yelpreviews.apiendpoint.DTO.ApiError;
import com.yelpreviews.apiendpoint.DTO.WrapperApiError;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearch;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearchList;
import com.yelpreviews.apiendpoint.DTO.YelpReview;
import com.yelpreviews.apiendpoint.apis.YelpApi;
import com.yelpreviews.apiendpoint.apis.YelpApi.CallType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@ComponentScan(basePackageClasses = com.yelpreviews.apiendpoint.controller.ControllerConfig.class)
public class Controller {
    
    public enum RouteType { SEARCH_BY_TERMS, SEARCH_BY_ID };
    @Autowired
    private ControllerConfig config;

    private Mono<ResponseEntity<Object>> subscribeToResponseBuilder(Mono<ResponseEntity<Object>> response) {
        return response;
    }

    /**
     * Controller mapping to search Yelp for a a list of business and their respective reviews via query params {term} and {location} -- where term is a food/drink Yelp category and location is a city name, zip code, street address, etc.
     * @param term specific restaurant name, food category, drink category, etc.
     * @param location city, zip code, etc.  Examples: "New York City", "NYC", "350 5th Ave, New York, NY 10118"
     * @return JSON String of some business info with the top 3 reviews from the Yelp API
     * @throws JsonProcessingException
     * @throws WebClientResponseException
     * @throws InvalidRequestParametersException
     * @throws MissingServletRequestPartException
     * @throws MissingServletRequestParameterException
     */
    @GetMapping(value = "/reviews", produces = "application/json")
    @Validated
    Mono<ResponseEntity<Object>> reviewsBySearchTerm(@RequestParam("term") String term, @RequestParam(value = "location", required = true) String location) throws JsonProcessingException, InvalidRequestParametersException {
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

        Mono<ResponseEntity<Object>> response = Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("term is missing or null in the request", "term", pathVars.get("term")))), HttpStatus.NOT_FOUND));
        try {
          // Mono<ResponseEntity<JsonNode>> bizMono = yelpApi.apiCall(RouteType.SEARCH_BY_TERMS, yelpApi.getBizSearchUriBuilder(), yelpApi.getReviewsSearchUriBuilder(), pathVars, yelpApi.getHttpMethod());
          response = yelpApi.apiCall(RouteType.SEARCH_BY_TERMS, yelpApi.getBizSearchUriBuilder(), yelpApi.getReviewsSearchUriBuilder(), pathVars, yelpApi.getHttpMethod()).flatMap((ResponseEntity<JsonNode> bizResponseEntity) -> {
          Mono<ResponseEntity<Object>> mappedResponse = Mono.empty();
          try {
            List<YelpBizSearch> dataList = new ArrayList<>();
            Map<String,Mono<YelpBizSearch>> bizResultMonos = new LinkedHashMap<String,Mono<YelpBizSearch>>();

            List<Map<String, ?>> prefetchedBizList = (ArrayList<Map<String,?>>)JSON.jsonToObject(bizResponseEntity.getBody().get("businesses"), ArrayList.class);
  
            List<Map<String,?>> searchLimitSublist = prefetchedBizList.subList(0, prefetchedBizList.size() < config.getSearchResultsLimit() ? prefetchedBizList.size()+1 : config.getSearchResultsLimit());
  
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
                  yelpBizSearch.setBizReviews(yelpReviews);
                  return yelpBizSearch;
                }
              );
              yelpBizSearchMono.doOnNext(yelpBizSearch -> dataList.add(yelpBizSearch)).subscribe();
            }
            YelpBizSearchList yelpBizSearchList = new YelpBizSearchList((int)bizResponseEntity.getBody().get("total").asInt(), config.getSearchResultsLimit(), dataList);
            mappedResponse = Mono.just(new ResponseEntity<Object>(yelpBizSearchList, HttpStatus.OK));
        } catch (JsonProcessingException e) {
          if(pathVars.get("term") == null){
              return Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("term is missing or null in the request", "term", pathVars.get("term")))), HttpStatus.NOT_FOUND));
          }
          if(pathVars.get("location") == null){
              return Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("location is missing or null in the request", "location", pathVars.get("location")))), HttpStatus.NOT_FOUND));
          }
        }
        return mappedResponse;
      });
      } catch (JsonProcessingException e) {
          if(pathVars.get("term") == null){
              return Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("term is missing or null in the request", "term", pathVars.get("term")))), HttpStatus.NOT_FOUND));
          }
          if(pathVars.get("location") == null){
              return Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("location is missing or null in the request", "location", pathVars.get("location")))), HttpStatus.NOT_FOUND));
          }
        }
      return response;
        // return config.getSearchByTermsRouteResponseBuilder().build(yelpApi, pathVars);
        // .doOnNext((ResponseEntity<Object>> res) -> { return subscribeToResponseBuilder(res); };
        // .flatMap((ResponseEntity<JsonNode> res) -> {
            // ResponseEntity<Object> response;
            // try {
                // response = config.getSearchByTermsRouteResponseBuilder().build(Mono.just(res), yelpApi, pathVars);
            // } catch (ResponseStatusException e) {
                // Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT_FOUND", e));
            // }
        // });
        
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
    Mono<ResponseEntity<Object>> reviewsByYelpBusinessId(@PathVariable @NotNull @NotBlank String bizId) throws JsonMappingException, JsonProcessingException, IllegalArgumentException {
        HashMap<String,String> pathVars = new HashMap<String,String>();
        pathVars.put("bizId", bizId);
        
        YelpApi yelpApi = new YelpApi(
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId"),
            (Map<String,String> uriVariables) -> "/" + uriVariables.get("bizId") + "/reviews",
            HttpMethod.GET, pathVars
        );

        Mono<ResponseEntity<JsonNode>> reviewsMono;
        Mono<ResponseEntity<Object>> response = Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("Could not find results for that Yelp business ID.", "businessId", pathVars.get("bizId")))), HttpStatus.NOT_FOUND));
        try {
        Mono<ResponseEntity<JsonNode>> bizMono = yelpApi.apiCall(RouteType.SEARCH_BY_TERMS, yelpApi.getBizSearchUriBuilder(), yelpApi.getReviewsSearchUriBuilder(), pathVars, yelpApi.getHttpMethod());
        reviewsMono = yelpApi.apiCall(RouteType.SEARCH_BY_ID, yelpApi.getReviewsSearchUriBuilder(), yelpApi.getReviewsSearchUriBuilder(), pathVars, yelpApi.getHttpMethod());

        response = bizMono.zipWith(reviewsMono).flatMap(tuple -> {
            ResponseEntity<JsonNode> bizResEntity = tuple.getT1();
            ResponseEntity<JsonNode> reviewsResEntity = tuple.getT2();
            Mono<ResponseEntity<Object>> bizSearch;
            if (bizResEntity.getStatusCode() != HttpStatus.OK || reviewsResEntity.getStatusCode() != HttpStatus.OK) {
            return Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("Could not find results for that Yelp business ID.", "businessId", pathVars.get("bizId")))), HttpStatus.NOT_FOUND));
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
            return Mono.just(new ResponseEntity<Object>(new ApiError(HttpStatus.NOT_FOUND, "VALIDATION_ERROR", List.of(new WrapperApiError("Could not find results for that Yelp business ID.", "businessId", pathVars.get("bizId")))), HttpStatus.NOT_FOUND));
            }
        }
        );
        } catch (ResponseStatusException | JsonProcessingException e1) {
            System.out.println(e1.getMessage());
        }
        return response;
    }


    @RequestMapping("*")
    public ResponseEntity<Object> getAnythingElse(HttpServletRequest request) {
        throw new PathNotFoundException(HttpStatus.NOT_FOUND, "A request to the specified path is not allowed.");
    }

}
package com.yelpreviews.apiendpoint.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yelpreviews.apiendpoint.DTO.ApiResponse;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearch;
import com.yelpreviews.apiendpoint.apis.YelpApi;
import com.yelpreviews.apiendpoint.exceptions.IncorrectDotEnvFileFormat;
import com.yelpreviews.apiendpoint.utils.DotEnvFileToSysProps;
import com.yelpreviews.apiendpoint.utils.JSON;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
public class MonoController {
    private static String API_KEY;

    static {
        API_KEY = System.getenv("YELP_API_KEY");
        // try {
        //     if(System.getenv("USERNAME").equalsIgnoreCase("brent")) {
        //         DotEnvFileToSysProps.setCredentialsAsSystemProperties();
        //         API_KEY = System.getProperty("YELP_API_KEY");
        //     }
        // } catch (IncorrectDotEnvFileFormat e) {
        //     System.out.println(e.getMessage());
        //     System.out.println("Could not get proper Yelp API Key credentials. Program closing.");
        //     System.exit(0);
        // }
    }

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
        Mono<String> bizSearchMono = YelpApi.getWebClient(YelpApi.getYelpBizSearchRootUrl())
                .get()
                .uri(uriBuilder -> uriBuilder.path("")
                    .queryParam("term", term)
                    .queryParam("location", location)
                    .build()
                )
                .header("Authorization", "Bearer " + API_KEY)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
        String bizSearchJson = bizSearchMono.block();
        String bizId = JSON.jsonToObject(JSON.parseJsonString(bizSearchJson), YelpBizSearch.class).getBizId();
        // Now call for reviews from the top business resulting from the our business search above
        Mono<String> reviewsSearchMono = YelpApi.getWebClient(YelpApi.getYelpReviewsRootUrl())
                .get()
                .uri(uriBuilder -> uriBuilder.path("/{bizId}/reviews").build(bizId))
                .header("Authorization", "Bearer " + API_KEY)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
        String reviewsSearchJson = reviewsSearchMono.block();
        ApiResponse apiResponse = new ApiResponse(JSON.parseJsonString(reviewsSearchJson).get("reviews"), 
        JSON.parseJsonString(bizSearchJson));

        return Mono.just(JSON.objectToJsonString(apiResponse, ApiResponse.class));
    }

    @GetMapping("/{bizId}/reviews")
    Mono<String> reviewsByYelpBusinessId(@PathVariable String bizId) throws JsonMappingException, JsonProcessingException, IllegalArgumentException {
        Mono<String> bizSearchMono = YelpApi.getWebClient(YelpApi.getYelpReviewsRootUrl())
                .get()
                .uri(uriBuilder -> uriBuilder.path("/{bizId}").build(bizId))
                .header("Authorization", "Bearer " + API_KEY)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
        // List<String> bizSingletonSearchList = new ArrayList<>();
        // bizSingletonSearchList.add(bizSearchMono.block());
        // String bizSearchJson = JSON.objectMapper.writeValueAsString(bizSingletonSearchList);
        String bizSearchJson = "{ \"businesses\": [" + bizSearchMono.block() + "]}";
        System.out.println("===============================================================================================");
        System.out.println(bizSearchJson);
                System.out.println("===============================================================================================");
        // String bizId = JSON.jsonToObject(JSON.parseJsonString(bizSearchJson),YelpBizSearch.class).getBizId();

        Mono<String> reviewsSearchMono = YelpApi.getWebClient(YelpApi.getYelpReviewsRootUrl())
                .get()
                .uri(uriBuilder -> uriBuilder.path("/{bizId}/reviews").build(bizId))
                .header("Authorization", "Bearer " + API_KEY)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
        String reviewsSearchJson = reviewsSearchMono.block();
        ApiResponse apiResponse = new ApiResponse(JSON.parseJsonString(reviewsSearchJson).get("reviews"), 
        JSON.parseJsonString(bizSearchJson));

        return Mono.just(JSON.objectToJsonString(apiResponse, ApiResponse.class));
    }

    @RequestMapping("**")
    public String getAnythingelse(){
        return "Request not found";
    }
}
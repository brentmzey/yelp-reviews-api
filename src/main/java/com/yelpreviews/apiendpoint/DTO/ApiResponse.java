package com.yelpreviews.apiendpoint.DTO;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.utils.JSON;

@JsonPropertyOrder({ "businessName", "businessId", "city", "reviews" })
public class ApiResponse {
    private List<YelpReview> yelpReviewList = new ArrayList<YelpReview>();
    private String businessName;
    private String businessId;
    private String city;


    public ApiResponse(JsonNode reviewArrayNode, JsonNode bizSearchNode) throws JsonMappingException, JsonProcessingException, IllegalArgumentException {
        Iterator<Map<String, ?>> reviewListIter = (Iterator<Map<String, ?>>)JSON.jsonToObject(reviewArrayNode, ArrayList.class).iterator();
        while(reviewListIter.hasNext())
            this.yelpReviewList.add(JSON.jsonToObject(JSON.parseJsonString(JSON.objectToJsonString(reviewListIter.next(), Map.class)), YelpReview.class));
            // this.yelpReviewList.add(JSON.jsonToObject(JSON.parseJsonString(JSON.objectMapper.writeValueAsString(reviewListIter.next())), YelpReview.class));
        YelpBizSearch yelpBizSearch = JSON.jsonToObject(bizSearchNode, YelpBizSearch.class);
        this.businessName = yelpBizSearch.getBizName();
        this.businessId = yelpBizSearch.getBizId();
        this.city = yelpBizSearch.getBizCity();
    }

    /**
     * Getters 
     */
    @JsonGetter("reviews")
    public List<YelpReview> getReviews() { return this.yelpReviewList; }
    @JsonGetter("businessName")
    public String getBusinessName() { return this.businessName; }
    @JsonGetter("businessId")
    public String getBusinessId() { return this.businessId; }
    @JsonGetter("city")
    public String getCity() { return this.city; }

}
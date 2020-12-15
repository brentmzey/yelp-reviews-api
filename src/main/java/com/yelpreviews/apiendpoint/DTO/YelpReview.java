package com.yelpreviews.apiendpoint.DTO;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonPropertyOrder({ "rating", "review", "userName", "userAvatarUrl" })
@JsonIgnoreProperties({ "userMap" })
public class YelpReview {
    private String userName;
    private String userAvatarUrl;
    private short rating;
    private String review;

    @JsonIgnore
    private Map<String, String> userMap;

    public YelpReview() {}

    /**
     * Getters & Setters
     */
    @JsonIgnore
    public Map<String, String> getUserMap() { return this.userMap; };
    @JsonGetter("userName")
    public String getUserName() { return this.userName; };
    @JsonGetter("userAvatarUrl")
    public String getUserAvatarUrl() { return this.userAvatarUrl; };
    @JsonGetter("rating")
    public short getRating() { return this.rating; }
    @JsonGetter("review")
    public String getReview() { return this.review; };
    
    @JsonSetter("user")
    public void setUserMap(Map<String, String> userMap) {
        this.userMap = userMap;
        this.userName = (String)userMap.get("name");
        this.userAvatarUrl = (String)userMap.get("image_url");
    };
    @JsonSetter("rating")
    public void setRating(short rating) { this.rating = (short)rating; };
    @JsonSetter("text")
    public void setReview(String review) { this.review = (String)review; };

}
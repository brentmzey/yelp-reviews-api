package com.yelpreviews.apiendpoint.DTO;

import java.util.Map;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonPropertyOrder({ "businessName", "businessId", "imageUrl", "address", "city", "zipCode", "state", "country", "reviews" })
@JsonIgnoreProperties({ "bizLocation" })
public class YelpBizSearch {
    private String bizId;
    private String bizName;
    private String bizAddress;
    private String bizCity;
    private String bizZipCode;
    private String bizState;
    private String bizCountry;
    private String bizImage;
    private List<YelpReview> bizReviews;
    private Map<String, ?> bizLocation;

    public YelpBizSearch() {}

    /**
     * Getters & Setters
     */
    public Map<String, ?> getBizLocation() { return this.bizLocation; }
    @JsonGetter("businessId")
    public String getBizId() { return this.bizId; }
    @JsonGetter("businessName")
    public String getBizName() { return this.bizName; }
    @JsonGetter("imageUrl")
    public String getBizImage() { return this.bizImage; }
    @JsonGetter("city")
    public String getBizCity() { return this.bizCity; }
    @JsonGetter("zipCode")
    public String getBizZipCode() { return this.bizZipCode; }
    @JsonGetter("reviews")
    public List<YelpReview> getBizReviews() { return this.bizReviews; }
    @JsonGetter("address")
    public String getBizAddress() {return this.bizAddress;}
    @JsonGetter("state")
    public String getState() {return this.bizState;}
    @JsonGetter("country")
    public String getCountry() {return this.bizCountry;}

    @JsonSetter("id")
    public void setBizId(String bizId) {
      this.bizId = bizId;
    }
    @JsonSetter("name")
    public void setBizName(String bizName) {
      this.bizName = bizName;
    }
    @JsonSetter("location")
    public void setBizCity(Map<String, ?> location) {
      this.bizLocation = location;
      this.bizAddress = (String)location.get("address1");
      this.bizCity = (String)location.get("city");
      this.bizZipCode = (String)location.get("zip_code");
      this.bizState = (String)location.get("state");
      this.bizCountry = (String)location.get("country");
    }
    @JsonSetter("image_url")
    public void setBizImage(String bizImage) { this.bizImage = bizImage; }
    public void setBizReviews(List<YelpReview> bizReviews) { this.bizReviews = bizReviews; }

}
package com.yelpreviews.apiendpoint.DTO;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonPropertyOrder({ "businessName", "businessId", "city" })
@JsonIgnoreProperties({ "bizLocation" })
public class YelpBizSearch {
    private String bizId;
    private String bizName;
    private String bizCity;
    private Map<String, ?> bizLocation;

    public YelpBizSearch() {}

    /**
     * Getters & Setters
     */
    @JsonGetter("businessId")
    public String getBizId() { return this.bizId; }
    @JsonGetter("businessName")
    public String getBizName() { return this.bizName; }
    // @JsonGetter("businessLocation")
    public Map<String, ?> getBizLocation() { return this.bizLocation; }
    @JsonGetter("city")
    public String getBizCity() { return this.bizCity; }

    @JsonSetter("businesses")
    public void setter(List<Map<String, ?>> businessesList) { 
        Map<String, ?> topResult = businessesList.get(0);
        this.bizId = (String)topResult.get("id");
        this.bizName = (String)topResult.get("name");
        Map<String, ?> bizLocationDetails = (Map<String, ?>)topResult.get("location");
        this.bizLocation = bizLocationDetails;
        this.bizCity = (String)bizLocationDetails.get("city");
    }

}
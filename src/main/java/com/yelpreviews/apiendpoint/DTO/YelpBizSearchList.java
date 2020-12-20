package com.yelpreviews.apiendpoint.DTO;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonPropertyOrder({ "count", "limit", "data" })
public class YelpBizSearchList {
    private int count;
    private int limit;
    private List<YelpBizSearch> data;

    public YelpBizSearchList() {}

    public YelpBizSearchList(int count, int limit, List<YelpBizSearch> data) {
        this.count = count;
        this.limit = limit;
        this.data = data;
    }
    
    public YelpBizSearchList(int count, List<YelpBizSearch> data) {
        this.count = count;
        this.limit = 10;
        this.data = data;
    }

    /**
     * Getters
     */
    @JsonGetter("count")
    public int getCount() { return count; }
    @JsonGetter("limit")
    public int getLimit() { return limit; }
    @JsonGetter("data")
    public List<YelpBizSearch> getData() { return this.data; }

    /**
     * Setters
     */
    @JsonSetter("count")
    public void setCount(String count) { 
        this.count = Integer.valueOf(count).intValue(); 
    }
    @JsonSetter("limit")
    public void setLimit(String limit) { 
        this.limit = Integer.valueOf(limit).intValue(); 
    }
    @JsonSetter("data")
    public void setData(List<YelpBizSearch> businessesList) { 
        this.data = businessesList;
    }

}
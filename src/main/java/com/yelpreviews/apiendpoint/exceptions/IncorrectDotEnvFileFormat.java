package com.yelpreviews.apiendpoint.exceptions;

public class IncorrectDotEnvFileFormat extends Exception {
    static final long serialVersionUID = 1L;
    public IncorrectDotEnvFileFormat(String key, String value) {
        super("Your '.env' file has the incorrect format. Please enter credentials as: key=value format with no spaces. There should only be one (key, value) pair exactly as: YELP_API_KEY=ACTUAL_KEY_VALUE.");
        System.out.println("Found system property of: " + key + "=" + value);
    }
}
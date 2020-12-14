package com.yelpreviews.apiendpoint.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSON {
    public static final ObjectMapper objectMapper;
    
    static {
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 
     * @param jsonString
     * @return the root node of the JSON string object representation. Then can further traverse the JSON off the return value of this method.
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    public static JsonNode parseJsonString(String jsonString) throws JsonMappingException, JsonProcessingException {
        return JSON.objectMapper.readTree(jsonString);
    }
    

    /**
     * 
     * @param <T>
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static <T> String objectToJsonString(Object object, Class<T> classToCastTo) throws JsonProcessingException {
        return JSON.objectMapper.writeValueAsString((T)object);
    }
   
    /**
     * 
     * @param object
     * @return JSON String from the Jackson API 
     * @throws JsonProcessingException
     */
    public static String toJson(Object object) throws JsonProcessingException {
        return JSON.objectMapper.writeValueAsString(object);
    }
    
    /**
     * 
     * @param object
     * @return JsonNode of root node of the JSON string passed in. Then can use Jackson API to traverse the JSON off the return value of this method.
     * @throws JsonProcessingException
     */
    public static JsonNode objectToJsonNode(Object object) throws JsonProcessingException {
        return JSON.parseJsonString(JSON.objectMapper.writeValueAsString(object));
    }

    /**
     * 
     * @param <A>
     * @param rootJsonString
     * @param clazz
     * @return
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     */
    public static <A> A stringToObject(String rootJsonString, Class<A> clazz) throws JsonProcessingException, IllegalArgumentException {
        return JSON.objectMapper.treeToValue(JSON.parseJsonString(rootJsonString), clazz);
    }

    /**
     * 
     * @param <A>
     * @param rootJsonNode
     * @param clazz
     * @return
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     */
    public static <A> A jsonToObject(JsonNode rootJsonNode, Class<A> clazz) throws JsonProcessingException, IllegalArgumentException {
        return JSON.objectMapper.treeToValue(rootJsonNode, clazz);
    }

    /**
     * Accepts an unlimited number of child field names to traverse from top level downward to grab the value of the last child field name you specify in String varargs form.
     * @param <A>
     * @param rootJsonNode
     * @param clazz
     * @param mapper customized Jackson ObjectMapper
     * @param childFieldNames varargs of Strings
     * @return
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     */
    public static <A> A jsonToObject(JsonNode rootJsonNode, Class<A> clazz, String ...childFieldNames) throws JsonProcessingException, IllegalArgumentException {
        JsonNode traversalNode = rootJsonNode;
        for (String jsonChildFieldName : childFieldNames){
            traversalNode = traversalNode.get(jsonChildFieldName);
        }
        return JSON.objectMapper.treeToValue(traversalNode, clazz);
    }

    /**
     * Accepts an unlimited number of child field names to traverse from top level downward to grab the value of the last child field name you specify in String varargs form.
     * @param <A>
     * @param rootJsonNode
     * @param clazz
     * @param mapper customized Jackson ObjectMapper
     * @param childFieldNames varargs of Strings
     * @return
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     */
    public static <A> A jsonToObject(String rootJsonString, Class<A> clazz, String ...childFieldNames) throws JsonProcessingException, IllegalArgumentException {
        JsonNode traversalNode = JSON.parseJsonString(rootJsonString);
        for (String jsonChildFieldName : childFieldNames){
            traversalNode = traversalNode.get(jsonChildFieldName);
        }
        return JSON.objectMapper.treeToValue(traversalNode, clazz);
    }
}
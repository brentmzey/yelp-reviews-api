package com.yelpreviews.apiendpoint.DTO;

// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
// import com.google.api.gax.core.CredentialsProvider;
// import com.google.cloud.vision.v1.AnnotateImageResponse;
// import com.google.cloud.vision.v1.FaceAnnotation;
// import com.google.cloud.vision.v1.ImageAnnotatorClient;
// import com.google.cloud.vision.v1.ImageAnnotatorSettings;
// import com.google.cloud.vision.v1.Feature.Type;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
// import org.springframework.core.io.DefaultResourceLoader;
// import org.springframework.core.io.ResourceLoader;
// import org.springframework.web.servlet.ModelAndView;

@JsonPropertyOrder({ "rating", "review", "userName", "userAvatarUrl" })
@JsonIgnoreProperties({ "userMap" })
public class YelpReview {
    private String userName;
    private String userAvatarUrl;
    private short rating;
    private String review;
    // private ModelAndView userAvatarEmotion;
    @JsonIgnore
    private Map<String, String> userMap;
    // @JsonIgnore
    // @Autowired private ResourceLoader resourceLoader = new DefaultResourceLoader();
    // @JsonIgnore
    // @Autowired private CloudVisionTemplate cloudVisionTemplate;

    public YelpReview() {
    //     try {
    //         ImageAnnotatorSettings settings = ImageAnnotatorSettings
    //                     .newBuilder()
    //                     .setCredentialsProvider()
    //                     .build();
    //         cloudVisionTemplate = new CloudVisionTemplate(ImageAnnotatorClient.create(settings));
    //     } catch (IOException e) {
    //       System.out.println(e.getMessage());
    //     }
    }

    // private ModelAndView callGoogleVisionApi(String imageUrl) {

    //     AnnotateImageResponse response = cloudVisionTemplate.analyzeImage(
    //     resourceLoader.getResource(imageUrl), Type.FACE_DETECTION);

    //     Map<String, Integer> faceAnnotations = new HashMap<String, Integer>();
    //     for(FaceAnnotation annotation : response.getFaceAnnotationsList()) {
    //         faceAnnotations.put("joyLikelihood", annotation.getJoyLikelihoodValue());
    //         faceAnnotations.put("surpriseLikelihood", annotation.getSurpriseLikelihoodValue());
    //         faceAnnotations.put("sorrowLikelihood", annotation.getSorrowLikelihoodValue());
    //         faceAnnotations.put("angerLikelihood", annotation.getAngerLikelihoodValue());
    //     }

    //     return new ModelAndView("likelihoods", faceAnnotations);
    // }

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
    // @JsonGetter("userAvatarEmotion")
    // public ModelAndView getuserAvatarEmotion() { return this.userAvatarEmotion; }
    
    @JsonSetter("user")
    public void setUserMap(Map<String, String> userMap) {
        this.userMap = userMap;
        this.userName = (String)userMap.get("name");
        this.userAvatarUrl = (String)userMap.get("image_url");
        // this.userAvatarEmotion = callGoogleVisionApi((String)userMap.get("image_url"));
    };
    @JsonSetter("rating")
    public void setRating(short rating) { this.rating = (short)rating; };
    @JsonSetter("text")
    public void setReview(String review) { this.review = (String)review; };

}
package com.yelpreviews.apiendpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.DTO.ApiResponse;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearch;
import com.yelpreviews.apiendpoint.DTO.YelpReview;
import com.yelpreviews.apiendpoint.utils.JSON;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiendpointApplicationTests {

	// @Test
	// void testYelpApiEndpointAuthorization() {
	// 	WebClient yelpReviews = WebClient.create("https://localhost:8080");
	// 	Mono<String> jsonResponse = yelpReviews.get().uri(uri, uriVariables)
	// }

	@Test
	void testJsonDeserializationBuisnessData() throws IOException {
		String testBizSearchJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleBizSearch.json")));
		JsonNode rootNode = JSON.parseJsonString(testBizSearchJsonResponse);
		YelpBizSearch bizSearch = JSON.jsonToObject(rootNode, YelpBizSearch.class);

		System.out.println(JSON.objectMapper.writeValueAsString(bizSearch));

		assertEquals("{\"businessName\":\"Four Barrel Coffee\",\"businessId\":\"E8RJkjfdcwgtyoPMjQ_Olg\",\"city\":\"San Francisco\"}", JSON.objectMapper.writeValueAsString(bizSearch));

	}
	
	@Test
	void testJsonDeserializationReviewsData() throws IOException {
		String testReviewsJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleReviews.json")));
		String testBizSearchJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleBizSearch.json")));
		JsonNode rootBizNode = JSON.parseJsonString(testBizSearchJsonResponse);
		JsonNode rootReviewsNode = JSON.parseJsonString(testReviewsJsonResponse).get("reviews");
		ApiResponse apiResponse = new ApiResponse(rootReviewsNode, rootBizNode);
		List<YelpReview> reviews = apiResponse.getReviews();

		System.out.println(JSON.objectMapper.writeValueAsString(reviews));

	}
	
	@Test
	void testApiResponseJson() throws IOException {
		String testReviewsJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleReviews.json")));
		String testBizSearchJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleBizSearch.json")));
		JsonNode rootBizNode = JSON.parseJsonString(testBizSearchJsonResponse);
		JsonNode rootReviewsNode = JSON.parseJsonString(testReviewsJsonResponse).get("reviews");
		ApiResponse apiResponse = new ApiResponse(rootReviewsNode, rootBizNode);

		System.out.println(JSON.objectToJsonString(apiResponse, ApiResponse.class));
	}

}

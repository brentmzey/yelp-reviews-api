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

		assertEquals("[{\"rating\":5,\"review\":\"Went back again to this place since the last time i visited the bay area 5 months ago, and nothing has changed. Still the sketchy Mission, Still the cashier...\",\"userName\":\"Ella A.\",\"userAvatarUrl\":\"https://s3-media3.fl.yelpcdn.com/photo/iwoAD12zkONZxJ94ChAaMg/o.jpg\"},{\"rating\":4,\"review\":\"The \\\"restaurant\\\" is inside a small deli so there is no sit down area. Just grab and go.\\n\\nInside, they sell individually packaged ingredients so that you can...\",\"userName\":\"Yanni L.\",\"userAvatarUrl\":null},{\"rating\":4,\"review\":\"Dear Mission District,\\n\\nI miss you and your many delicious late night food establishments and vibrant atmosphere.  I miss the way you sound and smell on a...\",\"userName\":\"Suavecito M.\",\"userAvatarUrl\":null}]", JSON.objectMapper.writeValueAsString(reviews));

	}
	
	@Test
	void testApiResponseJson() throws IOException {
		String testReviewsJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleReviews.json")));
		String testBizSearchJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleBizSearch.json")));
		JsonNode rootBizNode = JSON.parseJsonString(testBizSearchJsonResponse);
		JsonNode rootReviewsNode = JSON.parseJsonString(testReviewsJsonResponse).get("reviews");
		ApiResponse apiResponse = new ApiResponse(rootReviewsNode, rootBizNode);

		System.out.println(JSON.objectToJsonString(apiResponse, ApiResponse.class));
	
		assertEquals("{\"businessName\":\"Four Barrel Coffee\",\"businessId\":\"E8RJkjfdcwgtyoPMjQ_Olg\",\"city\":\"San Francisco\",\"reviews\":[{\"rating\":5,\"review\":\"Went back again to this place since the last time i visited the bay area 5 months ago, and nothing has changed. Still the sketchy Mission, Still the cashier...\",\"userName\":\"Ella A.\",\"userAvatarUrl\":\"https://s3-media3.fl.yelpcdn.com/photo/iwoAD12zkONZxJ94ChAaMg/o.jpg\"},{\"rating\":4,\"review\":\"The \\\"restaurant\\\" is inside a small deli so there is no sit down area. Just grab and go.\\n\\nInside, they sell individually packaged ingredients so that you can...\",\"userName\":\"Yanni L.\",\"userAvatarUrl\":null},{\"rating\":4,\"review\":\"Dear Mission District,\\n\\nI miss you and your many delicious late night food establishments and vibrant atmosphere.  I miss the way you sound and smell on a...\",\"userName\":\"Suavecito M.\",\"userAvatarUrl\":null}]}", JSON.objectToJsonString(apiResponse, ApiResponse.class));
	}

}

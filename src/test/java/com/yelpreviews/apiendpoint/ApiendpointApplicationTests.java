package com.yelpreviews.apiendpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.yelpreviews.apiendpoint.DTO.YelpBizSearch;
import com.yelpreviews.apiendpoint.DTO.YelpReview;
import com.yelpreviews.apiendpoint.utils.JSON;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiendpointApplicationTests {

	// ADD WEBTEST CLIENT tests to bind actions that simulate Client operations on the service
	// @Test
	// void testYelpApiEndpointAuthorization() {
	// 	WebClient yelpReviews = WebClient.create("https://localhost:8080");
	// 	Mono<String> jsonResponse = yelpReviews.get().uri(uri, uriVariables)
	// }

	@Test
	void testJsonDeserializationBuisnessData() throws IOException {
		String testBizSearchJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleBizSearch.json")));
		JsonNode rootNode = JSON.parseJsonString(testBizSearchJsonResponse);
		YelpBizSearch bizSearch = JSON.jsonToObject(rootNode.get("businesses").get(0), YelpBizSearch.class);

		System.out.println(JSON.objectMapper.writeValueAsString(bizSearch));

		assertEquals("{\"businessName\":\"Four Barrel Coffee\",\"businessId\":\"E8RJkjfdcwgtyoPMjQ_Olg\",\"city\":\"San Francisco\",\"zipCode\":\"94103\"}", JSON.objectMapper.writeValueAsString(bizSearch));

	}
	
	@Test
	void testJsonDeserializationReviewsData() throws IOException {
		String testReviewsJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleReviews.json")));
		String testBizSearchJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleBizSearch.json")));
		JsonNode rootBizNode = JSON.parseJsonString(testBizSearchJsonResponse).get("businesses");
		JsonNode rootReviewsNode = JSON.parseJsonString(testReviewsJsonResponse).get("reviews");
		// List<YelpReview> reviews = yelpBizSearch.getReviews();

		YelpBizSearch yelpBizSearch = JSON.jsonToObject(rootBizNode, YelpBizSearch.class);
		List<YelpReview> yelpReviewList = new ArrayList<>();
        Iterator<JsonNode> reviewListIter = rootReviewsNode.iterator();
            while(reviewListIter.hasNext()){
                yelpReviewList.add(JSON.jsonToObject(reviewListIter.next(), YelpReview.class));
            }
        yelpBizSearch.setBizReviews(yelpReviewList);
		System.out.println(JSON.toJson(yelpBizSearch.getBizReviews()));

		assertEquals("[{\"rating\":5,\"review\":\"Went back again to this place since the last time i visited the bay area 5 months ago, and nothing has changed. Still the sketchy Mission, Still the cashier...\",\"userName\":\"Ella A.\",\"userAvatarUrl\":\"https://s3-media3.fl.yelpcdn.com/photo/iwoAD12zkONZxJ94ChAaMg/o.jpg\"},{\"rating\":4,\"review\":\"The \\\"restaurant\\\" is inside a small deli so there is no sit down area. Just grab and go.\\n\\nInside, they sell individually packaged ingredients so that you can...\",\"userName\":\"Yanni L.\",\"userAvatarUrl\":null},{\"rating\":4,\"review\":\"Dear Mission District,\\n\\nI miss you and your many delicious late night food establishments and vibrant atmosphere.  I miss the way you sound and smell on a...\",\"userName\":\"Suavecito M.\",\"userAvatarUrl\":null}]", JSON.toJson(yelpBizSearch.getBizReviews()));

	}
	
	// @Test
	// void testApiResponseJson() throws IOException {
	// 	String testReviewsJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleReviews.json")));
	// 	String testBizSearchJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleBizSearch.json")));
	// 	JsonNode rootBizNode = JSON.parseJsonString(testBizSearchJsonResponse).get("businesses").get(0);
	// 	JsonNode rootReviewsNode = JSON.parseJsonString(testReviewsJsonResponse).get("reviews");
	// 	YelpBizById yelpBizById = new YelpBizById(rootReviewsNode, rootBizNode);

	// 	System.out.println(JSON.objectToJsonString(yelpBizById, YelpBizById.class));
	
	// 	assertEquals("{\"businessName\":\"Four Barrel Coffee\",\"businessId\":\"E8RJkjfdcwgtyoPMjQ_Olg\",\"city\":\"San Francisco\",\"reviews\":[{\"rating\":5,\"review\":\"Went back again to this place since the last time i visited the bay area 5 months ago, and nothing has changed. Still the sketchy Mission, Still the cashier...\",\"userName\":\"Ella A.\",\"userAvatarUrl\":\"https://s3-media3.fl.yelpcdn.com/photo/iwoAD12zkONZxJ94ChAaMg/o.jpg\"},{\"rating\":4,\"review\":\"The \\\"restaurant\\\" is inside a small deli so there is no sit down area. Just grab and go.\\n\\nInside, they sell individually packaged ingredients so that you can...\",\"userName\":\"Yanni L.\",\"userAvatarUrl\":null},{\"rating\":4,\"review\":\"Dear Mission District,\\n\\nI miss you and your many delicious late night food establishments and vibrant atmosphere.  I miss the way you sound and smell on a...\",\"userName\":\"Suavecito M.\",\"userAvatarUrl\":null}]}", JSON.objectToJsonString(yelpBizById, YelpBizById.class));
	// }

}

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
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
class ApiendpointApplicationTests {
	private static WebTestClient webTestClient;

	@BeforeAll
	public static void before() {
		String serverUrlToBind = System.getenv().get("YELP_API_KEY") != null ? "https://yelpreviews-api.herokuapp.com" : "http://localhost:8080";
		ApiendpointApplicationTests.webTestClient = WebTestClient.bindToServer().baseUrl(serverUrlToBind).build();
	}

	@Test
	void simWebClientActions() {
		ApiendpointApplicationTests.webTestClient.get()
		.uri(builder -> builder.path("/reviews").queryParam("term", "burger").queryParam("location", "milwaukee").build())
		.accept(MediaType.APPLICATION_JSON).exchange()
		.expectStatus().is2xxSuccessful();
		// .expectBody(YelpBizSearch.class);
	}

	@Test
	void testJsonDeserializationBuisnessData() throws IOException {
		String testBizSearchJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleBizSearchById.json")));
		String testReviewsJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleBizSearchByIdReviews.json")));
		
		JsonNode rootNode = JSON.parseJsonString(testBizSearchJsonResponse);

		YelpBizSearch yelpBizSearch = JSON.jsonToObject(rootNode, YelpBizSearch.class);
		List<YelpReview> yelpReviewList = new ArrayList<>();
		JsonNode rootReviewsNode = JSON.parseJsonString(testReviewsJsonResponse).get("reviews");
        Iterator<JsonNode> reviewListIter = rootReviewsNode.iterator();
            while(reviewListIter.hasNext()){
                yelpReviewList.add(JSON.jsonToObject(reviewListIter.next(), YelpReview.class));
            }
        yelpBizSearch.setBizReviews(yelpReviewList);

		System.out.println(JSON.toJson(yelpBizSearch));

		assertEquals("{\"businessName\":\"Brieux Carré Brewing Company\",\"businessId\":\"asz2Bsfk5vIn6Hh4K4BQeQ\",\"imageUrl\":\"https://s3-media1.fl.yelpcdn.com/bphoto/754x7Y2uKo3fj_lJgEbfGg/o.jpg\",\"address\":\"2115 Decatur St\",\"city\":\"New Orleans\",\"zipCode\":\"70116\",\"state\":\"LA\",\"country\":\"US\",\"reviews\":[{\"rating\":5,\"review\":\"My husband was annoyed at my insistence that Cafe Du Monde was the first thing we do in New Orleans so I appeased him by immediately taking him here after...\",\"userName\":\"Stacy O.\",\"userAvatarUrl\":\"https://s3-media1.fl.yelpcdn.com/photo/f8aEjovztyEfBydun3osTg/o.jpg\"},{\"rating\":5,\"review\":\"Enjoyed a quick pit stop here on our beer walk.   Good variety of IPA and wandered in at the perfect time the place wasn\'t super packed.  The staff was...\",\"userName\":\"Codie Nicole B.\",\"userAvatarUrl\":\"https://s3-media3.fl.yelpcdn.com/photo/iSFUxKR0etwg73CXX1cYCg/o.jpg\"},{\"rating\":5,\"review\":\"A really great time with really great beer. I went recently on a Saturday night and at 8 pm they had a free comedy show which was very entertaining (I...\",\"userName\":\"Steven G.\",\"userAvatarUrl\":\"https://s3-media3.fl.yelpcdn.com/photo/Uu-48h0r8klon9AKKhGlrA/o.jpg\"}]}", JSON.toJson(yelpBizSearch));

	}
	
	@Test
	void testJsonDeserializationReviewsData() throws IOException {
		String testReviewsJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleReviews.json")));
		String testBizSearchJsonResponse = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "sampleBizSearch.json")));
		JsonNode rootBizNode = JSON.parseJsonString(testBizSearchJsonResponse).get("businesses");
		JsonNode rootReviewsNode = JSON.parseJsonString(testReviewsJsonResponse).get("reviews");

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

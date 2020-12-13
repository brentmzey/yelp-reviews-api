package com.yelpreviews.apiendpoint;

import com.yelpreviews.apiendpoint.exceptions.IncorrectDotEnvFileFormat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiendpointApplication {

	public static void main(String[] args) throws IncorrectDotEnvFileFormat {
		SpringApplication.run(ApiendpointApplication.class, args);
	}

}

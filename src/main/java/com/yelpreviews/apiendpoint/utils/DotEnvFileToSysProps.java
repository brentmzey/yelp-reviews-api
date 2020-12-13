package com.yelpreviews.apiendpoint.utils;

import com.yelpreviews.apiendpoint.exceptions.IncorrectDotEnvFileFormat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class DotEnvFileToSysProps {
    private final static String dotEnvFile = System.getProperty("user.dir") + "\\.env";
        
    public static void setCredentialsAsSystemProperties() throws IncorrectDotEnvFileFormat {        
        try (BufferedReader buffRead = new BufferedReader(new FileReader(dotEnvFile))){
            buffRead.lines().forEach(line -> {
                String[] envVar = String.valueOf(line).split("=");
                String envVarKey = envVar[0];
                String envVarValue = envVar[1];
                Arrays.sort(envVar);
                if(Arrays.binarySearch(envVar, "YELP_API_KEY") > 0){
                    System.getProperties().setProperty(envVarKey, envVarValue);
                } else {
                    try {
                      throw new IncorrectDotEnvFileFormat(envVarKey, envVarValue);
                    } catch (IncorrectDotEnvFileFormat e) {
                      e.printStackTrace();
                      System.out.println(e);
                    }
                }
            });
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
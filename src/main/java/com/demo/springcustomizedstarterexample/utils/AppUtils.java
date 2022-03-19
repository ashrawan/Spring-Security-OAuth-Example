package com.demo.springcustomizedstarterexample.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;
import java.util.UUID;

public class AppUtils {

    private static ObjectMapper objectMapper;

    public AppUtils(ObjectMapper objectMapper) {
        AppUtils.objectMapper = objectMapper;
    }

    // Random UUID generator
    public static String generateRandomUUID() {
        return UUID.randomUUID().toString();
    }

    // Serializes an object
    public static String serialize(Serializable obj) {

        return Base64.getUrlEncoder().encodeToString(
                SerializationUtils.serialize(obj));
    }

    // Deserializes an object
    public static <T> T deserialize(String serializedObj) {

        return (T) SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(serializedObj));
    }

    // Serializes an object to JSON string
    public static <T> String toJson(T obj) {

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Deserializes a JSON String
    public static <T> T fromJson(String json,
                                 Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Generate random AlphaNumeric string
    public static String generateRandomAlphaNumericString(int n) {

        String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (alphaNumericString.length() * Math.random());
            sb.append(alphaNumericString.charAt(index));
        }
        return sb.toString();
    }

}

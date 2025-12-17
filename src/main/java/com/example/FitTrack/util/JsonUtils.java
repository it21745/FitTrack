package com.example.FitTrack.util;

import com.example.FitTrack.dto.FitnessProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(FitnessProfile fp) {
        try {
            return mapper.writeValueAsString(fp);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting FitnessProfile to JSON", e);
        }
    }

    public static FitnessProfile fromJson(String json) {
        try {
            if (json == null || json.isBlank()) {
                return new FitnessProfile();
            }
            return mapper.readValue(json, FitnessProfile.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON to FitnessProfile", e);
        }
    }
}

package com.example.FitTrack.service;

import com.example.FitTrack.dto.weather.WeatherDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.FitTrack.dto.API_dto.ForecastBlock;
import com.example.FitTrack.dto.API_dto.ForecastResponse;

import reactor.core.publisher.Mono;

@Service
public class WeatherService {

	private final double ATHENS_LATITUDE = 37.983810;
	private final double ATHENS_LONGITUDE = 23.727539;
	
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${weather.api.key}")
    private String apiKey;

    public WeatherService(
            @Value("${weather.api.url}") String baseUrl
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

//    public Mono<WeatherDto> getWeatherForCity(String city) {
//        return webClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/weather")
//                        .queryParam("q", city)
//                        .queryParam("appid", apiKey)
//                        .queryParam("units", "metric")
//                        .build()
//                )
//                .retrieve()
//                .bodyToMono(String.class)
//                .map(this::mapToDto);
//    }
//
//    private WeatherDto mapToDto(String json) {
//        try {
//            JsonNode root = objectMapper.readTree(json);
//
//            String description = root
//                    .path("weather")
//                    .get(0)
//                    .path("description")
//                    .asText();
//
//            double temperature = root
//                    .path("main")
//                    .path("temp")
//                    .asDouble();
//
//            WeatherDto dto = new WeatherDto();
//            dto.setDescription(description);
//            dto.setTemperature(temperature);
//
//            return dto;
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to parse weather response", e);
//        }
//    }
    
    //openweather returns a json of all predictions for the next few days
    //we receive it and find the closest moment to the one we are interested in
    public Mono<ForecastBlock> getAthensWeatherAtInstant(Instant targetInstant){
    	return webClient.get()
    			.uri(uriBuilder -> uriBuilder
    					.path("/forecast")
    					.queryParam("lat", ATHENS_LATITUDE)
    					.queryParam("lon", ATHENS_LONGITUDE)
    					.queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build()
    					)
    			.retrieve()
    			.bodyToMono(ForecastResponse.class)
    			.flatMap(response ->
	    		    Mono.justOrEmpty(findClosestForecast(response, targetInstant))
	    		);
    }
    
    
    private final long MAX_ALLOWED_TIME_DISTANCE = 5*24*3600; // =5 days
    
    
    private ForecastBlock findClosestForecast(ForecastResponse response, Instant targetInstant) {
    	if (response == null || response.getList() == null) {
    		return null;
    	}
    	
    	return response.getList().stream()
    			//find the forecastBlock with the lowest absolute time distance from the target instance
    			.min(Comparator.comparingLong(block ->
    				Math.abs(block.getInstant().getEpochSecond() - targetInstant.getEpochSecond())
    					))
    			//filter it out if it's more than 5 days before the target instant
    			.filter(block ->
    				Math.abs(block.getInstant().getEpochSecond() - targetInstant.getEpochSecond()) <= MAX_ALLOWED_TIME_DISTANCE
    					)
    			.orElse(null);
    }
    
    
    
    
    
    
    
    
    
    
    
    
}

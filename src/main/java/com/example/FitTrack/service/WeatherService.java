package com.example.FitTrack.service;

import com.example.FitTrack.dto.weather.WeatherDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherService {

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

    public Mono<WeatherDto> getWeatherForCity(String city) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("q", city)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .map(this::mapToDto);
    }

    private WeatherDto mapToDto(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);

            String description = root
                    .path("weather")
                    .get(0)
                    .path("description")
                    .asText();

            double temperature = root
                    .path("main")
                    .path("temp")
                    .asDouble();

            WeatherDto dto = new WeatherDto();
            dto.setDescription(description);
            dto.setTemperature(temperature);

            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse weather response", e);
        }
    }
}

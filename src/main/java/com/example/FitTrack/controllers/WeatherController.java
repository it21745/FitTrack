package com.example.FitTrack.controllers;

import com.example.FitTrack.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/api/weather/{city}")
    public Mono<String> getWeather(@PathVariable String city) {
        return weatherService.getWeatherForCity(city);
    }
}

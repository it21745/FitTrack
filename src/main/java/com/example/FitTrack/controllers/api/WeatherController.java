package com.example.FitTrack.controllers.api;

import com.example.FitTrack.dto.weather.WeatherDto;
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

//    @GetMapping("/api/weather/{city}")
//    public Mono<WeatherDto> getWeather(@PathVariable String city) {
//        return weatherService.getWeatherForCity(city);
//    }
}

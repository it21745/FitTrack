package com.example.FitTrack.dto.weather;

import com.example.FitTrack.dto.WeatherReportDto;

public class WeatherDto {

    private String description;
    private double temperature;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

}

package com.example.FitTrack.dto.API_dto.appointment;

import com.example.FitTrack.dto.WeatherReportDto;
import com.example.FitTrack.enums.TrainingType;

import java.time.LocalDateTime;

public class AppointmentDetailsDto {

    private Long id;
    private LocalDateTime appointmentDate;
    private TrainingType trainingType;
    private String trainerName;
    private String userName;
    private WeatherReportDto weather;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public WeatherReportDto getWeather() {
        return weather;
    }

    public void setWeather(WeatherReportDto weatherReport) {
        this.weather = weatherReport;
    }
}

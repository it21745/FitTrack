package com.example.FitTrack.dto.appointment;

import com.example.FitTrack.enums.TrainingType;

import java.time.LocalDateTime;

public class AppointmentResponseDto {

    private Long id;
    private LocalDateTime appointmentDate;
    private TrainingType trainingType;
    private String trainerName;
    private String traineeName;

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

    public String getTraineeName() {
        return traineeName;
    }

    public void setTraineeName(String userName) {
        this.traineeName = userName;
    }
}

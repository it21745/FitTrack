package com.example.FitTrack.dto;

public class FitnessProfile {

    private String goal;     // π.χ. weight_loss, muscle_gain, endurance, flexibility
    private Integer height;  // σε cm
    private Double weight;   // σε kg

    public FitnessProfile() {
    }

    public FitnessProfile(String goal, Integer height, Double weight) {
        this.goal = goal;
        this.height = height;
        this.weight = weight;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}

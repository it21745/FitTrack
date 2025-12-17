package com.example.FitTrack.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AvailabilityFormDto {
	//this class is a simpler form of the availability entity
	//the user will fill this in and it will be converted to an availability entity in the backend
	//otherwise stuff like id and myTrainer would be available to be edited from the frontend
	
	private boolean oneTime;
	
	//oneTime field
	private LocalDate date;
	
	//recurring field
    private DayOfWeek day;

    //common fields
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    
    @Min(value = 15, message = "Availability must be at least 15 minutes in duration")
    @Max(value = 360, message = "Availability must be less than 6 hours in duration")
    private int durationMinutes;
    
    
    //validation assertions
    
    @AssertTrue(message = "For one-time availabilities a date must be selected")
    public boolean isValid_oneTime() {
    	if (!isOneTime()) {
    		return true;
    	}
    	
    	if (date != null) {
    		return true;
    	}
    	return false;
    }
    
    @AssertTrue(message = "For recurring availabilities a day must be selected")
    public boolean isValid_recurring() {
    	if (isOneTime()) {return true;};
    	if (day != null) {return true;};
    	return false;
    }
    
    @AssertTrue(message = "One time availability cannot be set in the past")
    public boolean isNotInThePast() {
    	if (!oneTime || date == null || startTime == null) {
            return true;
        }
    	return !LocalDateTime.of(date, startTime).isBefore(LocalDateTime.now());
    	
    }
    
    
    //constructors
	public AvailabilityFormDto() {
	}

	public AvailabilityFormDto(boolean oneTime, LocalDate date, DayOfWeek day, LocalTime startTime,
			int durationMinutes) {
		this.oneTime = oneTime;
		this.date = date;
		this.day = day;
		this.startTime = startTime;
		this.durationMinutes = durationMinutes;
	}

	//getters and setters
	public boolean isOneTime() {
		return oneTime;
	}

	public void setOneTime(boolean oneTime) {
		this.oneTime = oneTime;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public DayOfWeek getDay() {
		return day;
	}

	public void setDay(DayOfWeek day) {
		this.day = day;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public int getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
	}
}

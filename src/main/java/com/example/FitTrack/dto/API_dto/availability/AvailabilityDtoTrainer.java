package com.example.FitTrack.dto.API_dto.availability;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import com.example.FitTrack.entities.Availability;

public class AvailabilityDtoTrainer {
	private int id;
	private int TrainerId;
	private boolean oneTime;
	
	private LocalDate date;
	private DayOfWeek day;
	private LocalTime startTime;
	private LocalTime endTime;
	
	private AvailabilityDtoTrainer() {}
	
	public static AvailabilityDtoTrainer toDto(Availability avail) {
		AvailabilityDtoTrainer dto = new AvailabilityDtoTrainer();
		dto.setId(avail.getId());
		dto.setTrainerId(avail.getMyTrainer().getId());
		dto.setOneTime(avail.isOneTime());
		if (avail.isOneTime()) {
			dto.setDate(avail.getDate());
			dto.setDay(null);
		}else {
			dto.setDate(null);
			dto.setDay(avail.getDay());
		}
		dto.setStartTime(avail.getStartTime());
		dto.setEndTime(avail.getEndTime());
		
		return dto;
	}
	
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTrainerId() {
		return TrainerId;
	}
	public void setTrainerId(int trainerId) {
		TrainerId = trainerId;
	}
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
	public LocalTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}
	
	
	
}

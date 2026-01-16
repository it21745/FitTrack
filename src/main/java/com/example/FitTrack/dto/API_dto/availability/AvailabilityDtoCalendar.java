package com.example.FitTrack.dto.API_dto.availability;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import com.example.FitTrack.entities.Availability;

//this is a more detailed dto that displays the offset for reccuring availabilities
//we call it "calendar" because it acts like the calendar in the thymeleaf side of the app
public class AvailabilityDtoCalendar {
	private int id;
	private Integer offset;
	private int TrainerId;
	private boolean oneTime;
	
	private LocalDate date;
	private DayOfWeek day;
	private LocalTime startTime;
	private LocalTime endTime;
	
	private AvailabilityDtoCalendar() {}
	
	public static List<AvailabilityDtoCalendar> toDto(Availability avail) {
		List<AvailabilityDtoCalendar> dtoList = new ArrayList<>();
		
		if (avail.isOneTime()) {
			AvailabilityDtoCalendar dto = new AvailabilityDtoCalendar();
			dto.setId(avail.getId());
			dto.setOffset(null);
			dto.setTrainerId(avail.getMyTrainer().getId());
			dto.setOneTime(true);
			dto.setDate(avail.getDate());
			dto.setDay(null);
			dto.setStartTime(avail.getStartTime());
			dto.setEndTime(avail.getEndTime());
			
			dtoList.add(dto);
		}else {
			LocalDate curDay = LocalDate.now();
			DayOfWeek targetDay = avail.getDay();
			LocalDate firstDate = curDay.with(TemporalAdjusters.nextOrSame(targetDay));
			
			for (int i=0;i<52;i++) {
				LocalDate eventDate = firstDate.plusWeeks(i);
				
				AvailabilityDtoCalendar dto = new AvailabilityDtoCalendar();
				dto.setId(avail.getId());
				dto.setOffset(i);
				dto.setTrainerId(avail.getMyTrainer().getId());
				dto.setOneTime(false);
				dto.setDate(eventDate);
				dto.setDay(avail.getDay());
				dto.setStartTime(avail.getStartTime());
				dto.setEndTime(avail.getEndTime());
				
				dtoList.add(dto);
			}
		}
		
		return dtoList;
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

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	
	
	
}

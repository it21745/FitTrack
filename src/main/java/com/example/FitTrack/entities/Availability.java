package com.example.FitTrack.entities;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;

@Entity
public class Availability {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int id;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "my_trainer_id", nullable = false)
	private SiteUser myTrainer;
	
	@Column(nullable = false)
	private boolean oneTime;
	
	@Column
	private LocalDate date;
	
	@Enumerated(EnumType.STRING)
	@Column
	private DayOfWeek day;
	
	@Column(nullable = false)
	private LocalTime startTime;
	
	@Column(nullable = false)
	private LocalTime endTime;

	
	
	//constructors
	public Availability(SiteUser myTrainer, boolean oneTime, LocalDate date, DayOfWeek day, LocalTime startTime,
			LocalTime endTime) {
		this.myTrainer = myTrainer;
		this.oneTime = oneTime;
		this.date = date;
		this.day = day;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public Availability() {}

	//getters and setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SiteUser getMyTrainer() {
		return myTrainer;
	}

	public void setMyTrainer(SiteUser myTrainer) {
		this.myTrainer = myTrainer;
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

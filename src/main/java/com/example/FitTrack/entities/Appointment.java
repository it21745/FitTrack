package com.example.FitTrack.entities;

import java.time.Instant;

import com.example.FitTrack.enums.AppointmentStatus;

import jakarta.persistence.*;

@Entity
@Table(name="appointments")
public class Appointment {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int id;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "my_trainer_id", nullable = false)
	private SiteUser myTrainer;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "my_trainee_id", nullable = false)
	private SiteUser myTrainee;
	
	@Column(nullable = false)
	private Instant startTime;
	
	@Column(nullable = false)
	private Instant endTime;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AppointmentStatus status;
	
	@Lob
	@Column
	private String log;

	
	
	//constructors
	public Appointment(SiteUser myTrainer, SiteUser myTrainee, Instant startTime, Instant endTime,
			AppointmentStatus status, String log) {
		this.myTrainer = myTrainer;
		this.myTrainee = myTrainee;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.log = log;
	}
	
	public Appointment() {}

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

	public SiteUser getMyTrainee() {
		return myTrainee;
	}

	public void setMyTrainee(SiteUser myTrainee) {
		this.myTrainee = myTrainee;
	}

	public Instant getStartTime() {
		return startTime;
	}

	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}

	public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}

	public AppointmentStatus getStatus() {
		return status;
	}

	public void setStatus(AppointmentStatus status) {
		this.status = status;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
	
}

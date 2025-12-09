package com.example.FitTrack.dto.validation;

import java.time.Instant;

import com.example.FitTrack.entities.Availability;
import com.example.FitTrack.entities.SiteUser;

public class AvailabilityValidationInfo {

	private SiteUser confirmedTrainer;
	private SiteUser confirmedTrainee;
	private Availability confirmedAvailability;
	private Instant startInstant;
	private Instant endInstant;
	
	
	public AvailabilityValidationInfo(SiteUser confirmedTrainer, SiteUser confirmedTrainee,
			Availability confirmedAvailability, Instant startInstant, Instant endInstant) {
		this.confirmedTrainer = confirmedTrainer;
		this.confirmedTrainee = confirmedTrainee;
		this.confirmedAvailability = confirmedAvailability;
		this.startInstant = startInstant;
		this.endInstant = endInstant;
	}
	


	public SiteUser getConfirmedTrainer() {
		return confirmedTrainer;
	}


	public void setConfirmedTrainer(SiteUser confirmedTrainer) {
		this.confirmedTrainer = confirmedTrainer;
	}


	public SiteUser getConfirmedTrainee() {
		return confirmedTrainee;
	}


	public void setConfirmedTrainee(SiteUser confirmedTrainee) {
		this.confirmedTrainee = confirmedTrainee;
	}


	public Availability getConfirmedAvailability() {
		return confirmedAvailability;
	}


	public void setConfirmedAvailability(Availability confirmedAvailability) {
		this.confirmedAvailability = confirmedAvailability;
	}


	public Instant getStartInstant() {
		return startInstant;
	}


	public void setStartInstant(Instant startInstant) {
		this.startInstant = startInstant;
	}


	public Instant getEndInstant() {
		return endInstant;
	}


	public void setEndInstant(Instant endInstant) {
		this.endInstant = endInstant;
	}
	
	
	
}

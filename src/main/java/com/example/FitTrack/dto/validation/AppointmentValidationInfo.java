package com.example.FitTrack.dto.validation;

import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.entities.SiteUser;

public class AppointmentValidationInfo {

	private Appointment myApp;
	private SiteUser myTrainer;
	private SiteUser myTrainee;
	private boolean isTrainer;
	
	public AppointmentValidationInfo(Appointment myApp, SiteUser myTrainer, SiteUser myTrainee, boolean isTrainer) {
		this.myApp = myApp;
		this.myTrainer = myTrainer;
		this.myTrainee = myTrainee;
		this.isTrainer = isTrainer;
		}
	public AppointmentValidationInfo() {
		
	}

	public Appointment getMyApp() {
		return myApp;
	}

	public void setMyApp(Appointment myApp) {
		this.myApp = myApp;
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

	public boolean isTrainer() {
		return isTrainer;
	}

	public void setIsTrainer(boolean isTrainer) {
		this.isTrainer = isTrainer;
	}


	
	
	
	
}

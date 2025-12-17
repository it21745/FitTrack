package com.example.FitTrack.dto.validation;

public class AppointmentValidationResult {

	private final boolean success;
	private final String reason;
	private final AppointmentValidationInfo info;
	
	
	public AppointmentValidationResult(boolean success, String reason, AppointmentValidationInfo info) {
		this.success = success;
		this.reason = reason;
		this.info = info;
	}


	public boolean isSuccess() {
		return success;
	}


	public String getReason() {
		return reason;
	}


	public AppointmentValidationInfo getInfo() {
		return info;
	}
	
	
	
}

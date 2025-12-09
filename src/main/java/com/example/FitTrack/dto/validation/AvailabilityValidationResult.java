package com.example.FitTrack.dto.validation;

public class AvailabilityValidationResult {
	
	private final boolean success;
	private final String reason;
	private final AvailabilityValidationInfo info;
	
	
	public AvailabilityValidationResult(boolean success, String reason, AvailabilityValidationInfo info) {
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


	public AvailabilityValidationInfo getInfo() {
		return info;
	}
	
	
	
	
}

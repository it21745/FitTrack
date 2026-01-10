package com.example.FitTrack.dto;

public class ErrorDto {
	private String reason;

	public ErrorDto(String reason) {
		this.setReason(reason);
	}
	
	public ErrorDto() {}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
	
	
}

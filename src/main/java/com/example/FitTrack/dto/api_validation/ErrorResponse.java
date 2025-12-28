package com.example.FitTrack.dto.api_validation;

import java.time.LocalDateTime;

public class ErrorResponse {
	private int status;
    private String message;
    private LocalDateTime timestamp;
    
    
    //constructors
    public ErrorResponse() {}
    
	public ErrorResponse(int status, String message) {
		this.status = status;
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}

	//getters and setters
	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public LocalDateTime getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
    
    
    
}

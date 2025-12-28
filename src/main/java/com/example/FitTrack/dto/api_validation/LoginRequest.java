package com.example.FitTrack.dto.api_validation;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
	
	@NotBlank(message = "Username is required")
	private String username;
	
	@NotBlank(message = "Password is required")
	private String password;
	
	
	//constructors
	public LoginRequest() {}


	public LoginRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}

	
	//getters and setters
	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
	
	
	
}

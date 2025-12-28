package com.example.FitTrack.dto.api_validation;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequest {
	
	@NotBlank(message = "Refresh token is required")
    private String refreshToken;

	
	//constructors
	public RefreshTokenRequest() {}
	
	public RefreshTokenRequest(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	//getters and setters
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	
	
	
}

package com.example.FitTrack.dto.api_validation;

public class AuthResponse {
	private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private String username;
    
    
    //constructors
    public AuthResponse() {}
    
	public AuthResponse(String accessToken, String refreshToken, String username) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.username = username;
	}

	//getters and setters
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
    
	
    
}

package com.example.FitTrack.controllers.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;

import com.example.FitTrack.config.jwt.JwtUtil;
import com.example.FitTrack.dto.api_validation.AuthResponse;
import com.example.FitTrack.dto.api_validation.ErrorResponse;
import com.example.FitTrack.dto.api_validation.LoginRequest;
import com.example.FitTrack.dto.api_validation.RefreshTokenRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private JwtUtil jwtUtil;
    
    
	public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
			JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtUtil = jwtUtil;
	}
    
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
		try {
			//authenticate
			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getUsername(),
							request.getPassword()
							)
					);
			
			//assuming an exception wasn't thrown (which would be caught bellow), the authentication was successful
			UserDetails details = userDetailsService.loadUserByUsername(request.getUsername());
			
			String accessToken = jwtUtil.generateAccessToken(details);
			String refreshToken = jwtUtil.generateRefreshToken(details);
			
			//return response
			AuthResponse response = new AuthResponse(accessToken, refreshToken, details.getUsername());
			return ResponseEntity.ok(response);
			
			
			
		} catch(BadCredentialsException e) {
			ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "invalid username or password");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		} catch (Exception e) {
			ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occured during logn");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}
	
	
	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request){
		try {
			String refreshToken = request.getRefreshToken();
			
			//load details and validate
			String username = jwtUtil.extractUsername(refreshToken);
			UserDetails details = userDetailsService.loadUserByUsername(username);
			
			if (jwtUtil.validateToken(refreshToken, details)) {
				//if validated, generate and return new token
				String newAccessToken = jwtUtil.generateAccessToken(details);
				
				AuthResponse response = new AuthResponse(newAccessToken, refreshToken, details.getUsername());
				return ResponseEntity.ok(response);
			}else {
				ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
			}
			
			
			
		}catch (Exception e) {
			ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}
	}
    
}

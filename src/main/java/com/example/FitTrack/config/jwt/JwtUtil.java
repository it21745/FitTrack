package com.example.FitTrack.config.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;
	
	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;
	
	
	
	private SecretKey getSigninKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
	
	//token creator
	private String createToken(Map<String, Object> claims, String subject, Long expiration) {
		return Jwts.builder()
				.claims(claims)
				.subject(subject)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis()+expiration))
				.signWith(getSigninKey())
				.compact();
	}
	
	
	//create tokens
	public String generateAccessToken(UserDetails details) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, details.getUsername(), accessTokenExpiration);
	}
	
	public String generateRefreshToken(UserDetails details) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, details.getUsername(), refreshTokenExpiration);
	}
	
	//extract data from token
	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSigninKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
				
	}
	
	private <T> T extractSpecificClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	public String extractUsername(String token) {
		return extractSpecificClaim(token, Claims::getSubject);
	}
	
	public Date extractExpiration(String token) {
		return extractSpecificClaim(token, Claims::getExpiration);
	}
	
	public boolean isExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	
	//validate token
	public boolean validateToken(String token, UserDetails details) {
		String username = extractUsername(token);
		if (username.equals(details.getUsername()) && !isExpired(token)) {
			return true;
		}
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}

package com.example.FitTrack.config.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil util;
	private final UserDetailsService detailService;
	
	
	public JwtAuthenticationFilter(JwtUtil util, UserDetailsService detailService) {
		this.util = util;
		this.detailService = detailService;
	}




	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		//get token
		String authHeader = request.getHeader("Authorization");
		String jwtToken;
		String username;
		
		//authHeader should look like "Bearer {random characters}"
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
		
		//remove the bearer part to get the token
		jwtToken = authHeader.substring(7);
		
		try {
			username = util.extractUsername(jwtToken);
			
			//check if username is valid and the token is not already authenticated
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails details = detailService.loadUserByUsername(username);
				
				//validate token against the given user
				if (util.validateToken(jwtToken, details)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						 details,
						 null,
						 details.getAuthorities());
					
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
			
			
		} catch (Exception e) {
			//we don't do anything, if the authentication fails the user just continues here without it
			//they won't be able to access parts that need authentication but these parts will let them know about it
			System.out.println("Jwt authentication failed");
			
		}
		
		//after the authentication just continue in the filter chain
		filterChain.doFilter(request, response);
		
		
	}

}

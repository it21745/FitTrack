package com.example.FitTrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.FitTrack.config.jwt.JwtAuthenticationFilter;
import com.example.FitTrack.service.SiteUserService;
import com.example.FitTrack.service.UserRoleService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
	
	private SiteUserService userService;
	private UserRoleService roleService;
	private BCryptPasswordEncoder passwordEncoder;
	private UserDetailsService userDetailsService;
	private JwtAuthenticationFilter jwtAuthFilter;

	
	
	

	public SecurityConfig(SiteUserService userService, UserRoleService roleService,
			BCryptPasswordEncoder passwordEncoder, UserDetailsService userDetailsService,
			JwtAuthenticationFilter jwtAuthFilter) {
		
		this.userService = userService;
		this.roleService = roleService;
		this.passwordEncoder = passwordEncoder;
		this.userDetailsService = userDetailsService;
		this.jwtAuthFilter = jwtAuthFilter;
	}


	//security chain for api
	//this will be called for requests on /api/** urls
	//disables csrf and is stateless with jwt
	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher("/api/**")
			.authorizeHttpRequests(requests -> requests
					.requestMatchers("/api/auth/**").permitAll()
					.anyRequest().authenticated())
			.sessionManagement(session -> 
					session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(csrf -> csrf.disable())
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	
	//non api requests go through here
	@Bean
	@Order(2)
	public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((requests) -> requests
				//pages that anyone can access
				.requestMatchers(
						"/",
						"/trainers",
						"/users/register",
						"/calendar/trainer/view/**",
						"/users/view/**",
						"/users/save",
						"/images/**",
						"/js/**",
						"/css/**"
						).permitAll()
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
					.loginPage("/login") //η custom login σελιδα μας
					.defaultSuccessUrl("/", true)  //σελιδα που εμφανιζεται αμεσως μετα το login
					.permitAll())
			.logout((logout) -> logout.permitAll());
		return http.build();
	}
	
	
	
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception{
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder)
				.and()
				.build();
	}


}
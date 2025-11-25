package com.example.FitTrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
	
	
	

	public SecurityConfig(SiteUserService userService, UserRoleService roleService,
			BCryptPasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
		this.userService = userService;
		this.roleService = roleService;
		this.passwordEncoder = passwordEncoder;
		this.userDetailsService = userDetailsService;
	}




	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((requests) -> requests
				//pages that anyone can access
				.requestMatchers("/", "/trainers", "/users/register", "/users/save", "/images/**", "/js/**", "/css/**").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
					.loginPage("/login") //η custom login σελιδα μας
					.defaultSuccessUrl("/", true)  //σελιδα που εμφανιζεται αμεσως μετα το login
					.permitAll())
			.logout((logout) -> logout.permitAll());
		return http.build();
	}


}
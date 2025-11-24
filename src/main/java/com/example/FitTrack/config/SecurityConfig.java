package com.example.FitTrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
	@Bean
	public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
		UserDetails user = User.withUsername("user")
				.password(passwordEncoder.encode("password"))
				.roles("USER")
				.build();
		UserDetails admin = User.withUsername("admin")
				.password(passwordEncoder.encode("admin"))
				.roles("USER", "ADMIN")
				.build();
		return new InMemoryUserDetailsManager(user, admin);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((requests) -> requests
				//pages that anyone can access
				.requestMatchers("/", "/Trainers", "/images/**", "/js/**", "/css/**").permitAll()
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
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return encoder;
    }
}
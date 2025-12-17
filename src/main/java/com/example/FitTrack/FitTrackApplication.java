package com.example.FitTrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FitTrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitTrackApplication.class, args);
	}

}

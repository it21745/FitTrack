package com.example.FitTrack.controllers.api;


import java.time.Instant;
import java.time.ZoneId;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.FitTrack.dto.validation.AvailabilityValidationInfo;
import com.example.FitTrack.dto.validation.AvailabilityValidationResult;
import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.entities.Availability;
import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.enums.AppointmentStatus;
import com.example.FitTrack.service.AppointmentService;
import com.example.FitTrack.service.ScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleRestController {
	private AppointmentService appService;
	private ScheduleService scheduleService;
	
	
	public ScheduleRestController(AppointmentService appService, ScheduleService scheduleService) {
		this.appService = appService;
		this.scheduleService = scheduleService;
	}
	
	
	
	
	@Operation(
    		summary = "Schedule appointment (Onetime)",
    	    description = "Attempts to schedule an appointment on a onetime availability",
    	    security = @SecurityRequirement(name = "bearerAuth")
    )
	@PostMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_TRAINEE')")
	public ResponseEntity<String> scheduleAppOnetime(
			@Parameter(
            		description = "ID of the onetime availability you want to request an appointment on",
            		example = "15",
            		required = true
            )
			@PathVariable int id, 
			@AuthenticationPrincipal User user
			){
		
		
		AvailabilityValidationResult result = scheduleService.verifyRequest(false,id,null,user);
		
		if (!result.isSuccess()) {
			return ResponseEntity.badRequest().body("You cannot set this appointment because:\n"+result.getReason());
		}
		
		//if valid create the appointment
		AvailabilityValidationInfo info = result.getInfo();
		
		Appointment newAppointment = new Appointment();
		newAppointment.setMyTrainer(info.getConfirmedTrainer());
		newAppointment.setMyTrainee(info.getConfirmedTrainee());
		newAppointment.setStartTime(info.getStartInstant());
		newAppointment.setEndTime(info.getEndInstant());
		newAppointment.setStatus(AppointmentStatus.Requested);
		
		try {
			appService.saveAppointment(newAppointment);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}
	
	
	
	
	
	
	@Operation(
    		summary = "Schedule appointment (Recurring)",
    	    description = "Attempts to schedule an appointment on a recurring availability",
    	    security = @SecurityRequirement(name = "bearerAuth")
    )
	@PostMapping("/{id}/{inst}")
	@PreAuthorize("hasRole('ROLE_TRAINEE')")
	public ResponseEntity<String> scheduleAppReccurring(
			@Parameter(
            		description = "ID of the recurring availability you want to request an appointment on",
            		example = "15",
            		required = true
            )
			@PathVariable int id,
			
			@Parameter(
            		description = "instance of the recurring availability you want to request an appointment on. 0 is the first instance, 1 is the second (1 week later) etc",
            		example = "15",
            		required = true
            )
			@PathVariable int inst, 
			@AuthenticationPrincipal User user
			){
		
		
		AvailabilityValidationResult result = scheduleService.verifyRequest(true,id,inst,user);
		
		if (!result.isSuccess()) {
			return ResponseEntity.badRequest().body("You cannot set this appointment because:\n"+result.getReason());
		}
		
		//if valid create the appointment
		//get info
		AvailabilityValidationInfo info = result.getInfo();
		SiteUser confirmedTrainer = info.getConfirmedTrainer();
		SiteUser confirmedTrainee = info.getConfirmedTrainee();
		Availability confirmedAvail = info.getConfirmedAvailability();
		Instant startInstant = info.getStartInstant();
		Instant endInstant = info.getEndInstant();
		String weatherInfo = info.getWeatherInfo();
		
		ZoneId athensZone = ZoneId.of("Europe/Athens");
		
		
		//create appointment
		Appointment newAppointment = new Appointment();
		newAppointment.setMyTrainer(confirmedTrainer);
		newAppointment.setMyTrainee(confirmedTrainee);
		newAppointment.setStartTime(startInstant);
		newAppointment.setEndTime(endInstant);
		newAppointment.setStatus(AppointmentStatus.Requested);
		
		try {
			appService.saveAppointment(newAppointment);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}
	
	
}

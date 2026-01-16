package com.example.FitTrack.controllers.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.FitTrack.dto.API_dto.availability.AvailabilityDtoTrainer;
import com.example.FitTrack.dto.API_dto.availability.AvailabilityDtoCalendar;
import com.example.FitTrack.entities.Availability;
import com.example.FitTrack.service.AvailabilityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/availabilities")
public class AvailabilityRestController {

	private AvailabilityService availService;

	public AvailabilityRestController(AvailabilityService availService) {
		this.availService = availService;
	}
	
	@Operation(
    		summary = "Get trainer availabilities",
    	    description = "Returns all availabilities of a trainer"
    )
	@GetMapping("/user/{id}")
	public ResponseEntity<List<AvailabilityDtoTrainer>> getTrainerAvailabilities(
			@Parameter(
            		description = "ID of the trainer whose availabilities you want to view",
            		example = "15",
            		required = true
            )
			@PathVariable int id
			){
		
		try {
			Optional<List<Availability>> avails = availService.getAvailByUserId(id);
			
			if (avails.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			
			//toDto
			List<AvailabilityDtoTrainer> dtoList = new ArrayList<>();
			for (Availability a: avails.get()) {
				dtoList.add(AvailabilityDtoTrainer.toDto(a));
			}
			
			return ResponseEntity.ok(dtoList);
			
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		
	}
	
	
	
	@Operation(
    		summary = "View availability",
    	    description = "Returns an availability in a more detailed way"
    )
	@GetMapping("{id}")
	public ResponseEntity<List<AvailabilityDtoCalendar>> viewAvailability(
			@Parameter(
            		description = "ID of the availability you want to view",
            		example = "15",
            		required = true
            )
			@PathVariable int id
			){
		
		Optional<Availability> givenAvail = availService.getAvailById(id);
		
		if (givenAvail.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		Availability confirmedAvail = givenAvail.get();
		return ResponseEntity.ok(AvailabilityDtoCalendar.toDto(confirmedAvail));
	}
}

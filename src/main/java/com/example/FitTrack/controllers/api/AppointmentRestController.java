package com.example.FitTrack.controllers.api;

import com.example.FitTrack.dto.API_dto.appointment.AppointmentDetailsDto;
import com.example.FitTrack.dto.API_dto.appointment.AppointmentResponseDto;
import com.example.FitTrack.dto.API_dto.appointment.AppointmentUpdateRequestDto;
import com.example.FitTrack.dto.validation.AppointmentValidationInfo;
import com.example.FitTrack.dto.validation.AppointmentValidationResult;
import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.enums.AppointmentStatus;
import com.example.FitTrack.enums.AppointmentUpdateAction;
import com.example.FitTrack.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentRestController {

    private final AppointmentService appointmentService;

    // Constructor injection (χωρίς Lombok)
    public AppointmentRestController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    
    

    @Operation(
    		summary = "Get trainee appointments",
    	    description = "Returns all appointments when the current user is a trainee",
    	    security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_TRAINEE')")
    public ResponseEntity<List<AppointmentResponseDto>> getUserAppointments() {
    	return ResponseEntity.ok(appointmentService.getUserAppointments());
    }

    
    
    
    @Operation(
    		summary = "Get trainer appointments",
    	    description = "Returns all appointments when the current user is a trainer",
    	    security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/trainer")
    @PreAuthorize("hasRole('ROLE_TRAINER')")
    public ResponseEntity<List<AppointmentResponseDto>> getTrainerAppointments() {
        return ResponseEntity.ok(appointmentService.getTrainerAppointments());
    }

    
    
    @Operation(
    		summary = "View appointment details",
    	    description = "Returns the details for any appointment provided it belongs to you",
    	    security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_TRAINEE','ROLE_TRAINER')")
    public ResponseEntity<AppointmentDetailsDto> getAppointmentDetails(
            @Parameter(
            		description = "ID of the appointment to view",
            		example = "15",
            		required = true
            )
    		@PathVariable Long id
    ) {
    	try {
    	AppointmentDetailsDto details = appointmentService.getAppointmentDetails(id);
    	return ResponseEntity.ok(details);
    	} catch (RuntimeException e) {
    		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
    }
    
    
    
    
    
    
    @Operation(
    		summary = "Update Appointment Status",
    	    description = """
    	    		Allows you to update the status of an appointment in certain ways
    	    		
    	    		Trainers can:
    	    		Confirm/Reject a requested appointment,
    	    		Cancel a confirmed appointment
    	    		
    	    		Trainees can:
    	    		Cancel a requested or confirmed appointment
    	    		""",
    	    security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_TRAINEE','ROLE_TRAINER')")
    public ResponseEntity<String> updateAppointment(
    		@Parameter(
            		description = "ID of the appointment to update",
            		example = "15",
            		required = true
            )
    		@PathVariable int id, 
    		@AuthenticationPrincipal User user,
    		Authentication authentication,
    		@RequestBody @Valid AppointmentUpdateRequestDto appointmentUpdateRequestDto){
    	
    	
    	AppointmentValidationResult result = appointmentService.validateRequest(user, authentication, id);
    	if (!result.isSuccess()) {
    		return ResponseEntity.badRequest().body("You cannot update this appointment because:\n"+result.getReason());
		}
    	
    	AppointmentValidationInfo info = result.getInfo();
		Appointment app = info.getMyApp();
		
		//make sure appointment is up to date
		appointmentService.syncAppointmentStatus(app);
		
		//change appointment status
		if (info.isTrainer()) {
			switch (appointmentUpdateRequestDto.getAction()) {
				case AppointmentUpdateAction.Confirm:
					if (app.getStatus().equals(AppointmentStatus.Requested)) {
						app.setStatus(AppointmentStatus.Accepted);
						
						//when a trainer confirms an appointment it should also automaticaly reject other appointments on this availability
						List<Appointment> conflictingApps = appointmentService.getOverlappingAppointmentsByTrainerAndTime(info.getMyTrainer().getId(), app.getStartTime(), app.getEndTime());
						for (Appointment a: conflictingApps) {
							if (!a.equals(app)) {
								if (a.getStatus().equals(AppointmentStatus.Requested)) {
									a.setStatus(AppointmentStatus.Rejected);
								}
							}
						}
					}else {
						return ResponseEntity.badRequest().body("Invalid input");
					}
					break;
				case AppointmentUpdateAction.Reject:
					if (app.getStatus().equals(AppointmentStatus.Requested)) {
						app.setStatus(AppointmentStatus.Rejected);
					}else {
						return ResponseEntity.badRequest().body("Invalid input");
					}
					break;
				case AppointmentUpdateAction.Cancel:
					if (app.getStatus().equals(AppointmentStatus.Accepted)) {
						app.setStatus(AppointmentStatus.Canceled);
					}else {
						return ResponseEntity.badRequest().body("Invalid input");
					}
					break;
				default:
					return ResponseEntity.badRequest().body("Invalid input");
			}
		}else {
			switch (appointmentUpdateRequestDto.getAction()) {
				case AppointmentUpdateAction.Cancel:
					if (app.getStatus().equals(AppointmentStatus.Accepted) || app.getStatus().equals(AppointmentStatus.Requested)) {
						app.setStatus(AppointmentStatus.Canceled);
					}else {
						return ResponseEntity.badRequest().body("Invalid input");
					}
					break;
				default:
					return ResponseEntity.badRequest().body("Invalid input");
			}
		}
		
		
		try {
			appointmentService.saveAppointment(app);
		}catch (Exception e) {
			return ResponseEntity.badRequest().body("There was a problem with updating the appointment");
		}
    	
    	
		return ResponseEntity.ok().build();
    }
    
}


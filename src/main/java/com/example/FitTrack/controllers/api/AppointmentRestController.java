package com.example.FitTrack.controllers.api;

import com.example.FitTrack.dto.appointment.AppointmentDetailsDto;
import com.example.FitTrack.dto.appointment.AppointmentRequestDto;
import com.example.FitTrack.dto.appointment.AppointmentResponseDto;
import com.example.FitTrack.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AppointmentResponseDto> createAppointment(
            @Valid @RequestBody AppointmentRequestDto dto
    ) {
        return ResponseEntity.ok(appointmentService.create(dto));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AppointmentResponseDto>> getUserAppointments() {
        return ResponseEntity.ok(appointmentService.getUserAppointments());
    }

    @GetMapping("/trainer")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<List<AppointmentResponseDto>> getTrainerAppointments() {
        return ResponseEntity.ok(appointmentService.getTrainerAppointments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','TRAINER')")
    public ResponseEntity<AppointmentDetailsDto> getAppointmentDetails(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(appointmentService.getAppointmentDetails(id));
    }
}

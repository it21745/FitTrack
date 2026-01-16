package com.example.FitTrack.dto.API_dto.appointment;

import com.example.FitTrack.enums.AppointmentUpdateAction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class AppointmentUpdateRequestDto {

	@Schema(
        description = "Update Action",
        example = "Confirm"
    )
	@NotNull
	private AppointmentUpdateAction action;

	public AppointmentUpdateAction getAction() {
		return action;
	}

	public void setAction(AppointmentUpdateAction action) {
		this.action = action;
	}
	
	

	
	
}

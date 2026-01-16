package com.example.FitTrack.controllers;

import java.time.ZoneId;
import java.time.Instant;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.FitTrack.dto.validation.AvailabilityValidationInfo;
import com.example.FitTrack.dto.validation.AvailabilityValidationResult;
import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.entities.Availability;
import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.enums.AppointmentStatus;
import com.example.FitTrack.service.AppointmentService;
import com.example.FitTrack.service.ScheduleService;


@Controller
@RequestMapping("/schedule")
public class ScheduleController {
	private AppointmentService appService;
	private ScheduleService scheduleService;
	
	
	public ScheduleController(AppointmentService appService, ScheduleService scheduleService) {
		this.appService = appService;
		this.scheduleService = scheduleService;
	}
	
	
	@PreAuthorize("hasRole('ROLE_TRAINEE')")
	@GetMapping("/{id}")
	public String createAppointment(@PathVariable Integer id, @AuthenticationPrincipal User user, Model model) {
		//verify we can schedule an appointment with this info
		AvailabilityValidationResult result = scheduleService.verifyRequest(false,id,null,user);
		
		//if not say why
		if (!result.isSuccess()) {
			model.addAttribute("availableCheck", false);
			model.addAttribute("failReason",result.getReason());
			return "/schedule/requestAppointment";
		}
		
		//if yes get the info and show it to the user
		AvailabilityValidationInfo info = result.getInfo();
		SiteUser confirmedTrainer = info.getConfirmedTrainer();
		SiteUser confirmedTrainee = info.getConfirmedTrainee();
		Availability confirmedAvail = info.getConfirmedAvailability();
		Instant startInstant = info.getStartInstant();
		Instant endInstant = info.getEndInstant();
		String weatherInfo = info.getWeatherInfo();
		
		
		
		
		
		model.addAttribute("availableCheck", true);
		model.addAttribute("trainerName", confirmedTrainer.getFirstName() + " " + confirmedTrainer.getLastName());
		model.addAttribute("date", confirmedAvail.getDate().toString());
		model.addAttribute("startTime", confirmedAvail.getStartTime().toString());
		model.addAttribute("endTime", confirmedAvail.getEndTime().toString());
		model.addAttribute("weather", weatherInfo);
		
		
		return "/schedule/requestAppointment";
	}
	
	
	
	
	
	@PreAuthorize("hasRole('ROLE_TRAINEE')")
	@PostMapping("/{id}")
	public String createAppointmentPost(@PathVariable Integer id, @AuthenticationPrincipal User user, Model model, RedirectAttributes redirectAttributes) {
		AvailabilityValidationResult result = scheduleService.verifyRequest(false,id,null,user);
		
		if (!result.isSuccess()) {
			redirectAttributes.addAttribute("availableCheck", false);
			redirectAttributes.addAttribute("failReason",result.getReason());
			return "redirect:/schedule/requestAppointment";
		}
		
		AvailabilityValidationInfo info = result.getInfo();
		SiteUser confirmedTrainer = info.getConfirmedTrainer();
		SiteUser confirmedTrainee = info.getConfirmedTrainee();
		Availability confirmedAvail = info.getConfirmedAvailability();
		Instant startInstant = info.getStartInstant();
		Instant endInstant = info.getEndInstant();
		String weatherInfo = info.getWeatherInfo();
		
		
		//add info to redirect, whether we succeed or fail we will show it again
		redirectAttributes.addAttribute("availableCheck", true);		
		redirectAttributes.addAttribute("trainerName", confirmedTrainer.getFirstName() + " " + confirmedTrainer.getLastName());
		redirectAttributes.addAttribute("date", confirmedAvail.getDate().toString());
		redirectAttributes.addAttribute("startTime", confirmedAvail.getStartTime().toString());
		redirectAttributes.addAttribute("endTime", confirmedAvail.getEndTime().toString());
		model.addAttribute("weather", weatherInfo);
		
		//create new appointment with the given info and try to save it in the db
		Appointment newAppointment = new Appointment();
		newAppointment.setMyTrainer(confirmedTrainer);
		newAppointment.setMyTrainee(confirmedTrainee);
		newAppointment.setStartTime(startInstant);
		newAppointment.setEndTime(endInstant);
		newAppointment.setStatus(AppointmentStatus.Requested);
		
		try {
			Appointment savedAppointment = appService.saveAppointment(newAppointment);
			return "redirect:/schedule/appointmentRequestedSuccessfully";
		} catch (Exception e) {
			redirectAttributes.addAttribute("availableCheck",false);
			redirectAttributes.addAttribute("failReason","appointment creation failed");
			return "redirect:/schedule/requestAppointment/"+id;
		}
	}
	
	
	
	
	
	
	
	@PreAuthorize("hasRole('ROLE_TRAINEE')")
	@GetMapping("/{id}/{inst}")
	public String createAppointmentFromRecurringAvailability(@PathVariable Integer id, @PathVariable Integer inst, @AuthenticationPrincipal User user, Model model) {
		AvailabilityValidationResult result = scheduleService.verifyRequest(true,id,inst,user);
		
		if (!result.isSuccess()) {
			model.addAttribute("availableCheck", false);
			model.addAttribute("failReason",result.getReason());
			return "/schedule/requestAppointment";
		}
		
		AvailabilityValidationInfo info = result.getInfo();
		SiteUser confirmedTrainer = info.getConfirmedTrainer();
		SiteUser confirmedTrainee = info.getConfirmedTrainee();
		Availability confirmedAvail = info.getConfirmedAvailability();
		Instant startInstant = info.getStartInstant();
		Instant endInstant = info.getEndInstant();
		String weatherInfo = info.getWeatherInfo();
		
		
		ZoneId athensZone = ZoneId.of("Europe/Athens");
		
		
		model.addAttribute("availableCheck", true);
		
		model.addAttribute("trainerName", confirmedTrainer.getFirstName() + " " + confirmedTrainer.getLastName());
		//changed this attribute to display the correct date
		model.addAttribute("date", startInstant.atZone(athensZone).toLocalDate().toString());
		model.addAttribute("startTime", confirmedAvail.getStartTime().toString());
		model.addAttribute("endTime", confirmedAvail.getEndTime().toString());
		model.addAttribute("weather", weatherInfo);
		
		
		return "/schedule/requestAppointment";
	}
	
	
	
	
	
	
	
	
	
	@PreAuthorize("hasRole('ROLE_TRAINEE')")
	@PostMapping("/{id}/{inst}")
	public String createAppointmentFromRecurringAvailabilityPost(@PathVariable Integer id, @PathVariable Integer inst, @AuthenticationPrincipal User user, Model model, RedirectAttributes redirectAttributes) {
		AvailabilityValidationResult result = scheduleService.verifyRequest(true,id,inst,user);
		
		if (!result.isSuccess()) {
			redirectAttributes.addAttribute("availableCheck", false);
			redirectAttributes.addAttribute("failReason",result.getReason());
			return "redirect:/schedule/requestAppointment";
		}
		
		AvailabilityValidationInfo info = result.getInfo();
		SiteUser confirmedTrainer = info.getConfirmedTrainer();
		SiteUser confirmedTrainee = info.getConfirmedTrainee();
		Availability confirmedAvail = info.getConfirmedAvailability();
		Instant startInstant = info.getStartInstant();
		Instant endInstant = info.getEndInstant();
		String weatherInfo = info.getWeatherInfo();
		
		
		
		ZoneId athensZone = ZoneId.of("Europe/Athens");
		
		
		redirectAttributes.addAttribute("availableCheck", true);
		
		redirectAttributes.addAttribute("trainerName", confirmedTrainer.getFirstName() + " " + confirmedTrainer.getLastName());
		redirectAttributes.addAttribute("date", startInstant.atZone(athensZone).toLocalDate().toString());
		redirectAttributes.addAttribute("startTime", confirmedAvail.getStartTime().toString());
		redirectAttributes.addAttribute("endTime", confirmedAvail.getEndTime().toString());
		redirectAttributes.addAttribute("weather", weatherInfo);
		
		
		
		
		
		Appointment newAppointment = new Appointment();
		newAppointment.setMyTrainer(confirmedTrainer);
		newAppointment.setMyTrainee(confirmedTrainee);
		newAppointment.setStartTime(startInstant);
		newAppointment.setEndTime(endInstant);
		newAppointment.setStatus(AppointmentStatus.Requested);
		
		try {
			Appointment savedAppointment = appService.saveAppointment(newAppointment);
			return "redirect:/schedule/appointmentRequestedSuccessfully";
		} catch (Exception e) {
			redirectAttributes.addAttribute("availableCheck",false);
			redirectAttributes.addAttribute("failReason","appointment creation failed");
			return "redirect:/schedule/requestAppointment/"+id+"/"+inst;
		}
		
	}
	
	
	
	
	
	//if we don't have this method here then when we return "redirect:/schedule/appointmentRequestedSuccessfully"
	//it thinks we are trying to access a /schedule/{id} page and tries to turn the "appointmentRequestedSuccessfully" string
	//into an integer
	@GetMapping("/appointmentRequestedSuccessfully")
	public String appointmentRequestedSuccessfully() {
	    return "/schedule/appointmentRequestedSuccessfully";
	}

	
	
	
	
}


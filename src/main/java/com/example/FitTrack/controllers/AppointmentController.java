package com.example.FitTrack.controllers;

import java.time.Instant;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.FitTrack.dto.WeatherReportDto;
import com.example.FitTrack.dto.validation.AppointmentValidationInfo;
import com.example.FitTrack.dto.validation.AppointmentValidationResult;
import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.enums.AppointmentStatus;
import com.example.FitTrack.service.AppointmentService;
import com.example.FitTrack.service.WeatherService;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

	
	private AppointmentService appService;
	private WeatherService weatherService;
	
	
	public AppointmentController(AppointmentService appService, WeatherService weatherService) {
		this.appService = appService;
		this.weatherService = weatherService;
	}
	
	
	
	
	@PreAuthorize("isAuthenticated()")  //different behavior depending on whether a trainer or a trainee wants to view an appointment
	@GetMapping("/{id}/view")
	public String viewAppointment(@AuthenticationPrincipal User user, Model model, Authentication authentication, @PathVariable Integer id) {
		AppointmentValidationResult result = appService.validateRequest(user, authentication, id);
		if (!result.isSuccess()) {
			model.addAttribute("errorReason",result.getReason());
			return "/error";
		}
		
		//checks passed, get info
		AppointmentValidationInfo appInfo = result.getInfo();
		
		//also check weather
		WeatherReportDto weatherReport;
		if (!appInfo.getMyApp().getStartTime().isBefore(Instant.now())) {
			weatherReport = weatherService.getAthensWeatherAtInstant(appInfo.getMyApp().getStartTime())
					.map(WeatherReportDto::createReport)
					.defaultIfEmpty(WeatherReportDto.unavailable())
					.block();
		}else {
			weatherReport = WeatherReportDto.createNullReport();
		}
		
		
		
		//show appointment		
		model.addAttribute("appointment", appInfo.getMyApp());  //every attribute of an appointment (id, trainer, trainee, start, end,status, log) will need to be displayed here, so there's no point in adding a dto to hide anything
		model.addAttribute("AppointmentStatus", AppointmentStatus.class);
		model.addAttribute("trainerName", appInfo.getMyTrainer().getUsername());
		model.addAttribute("traineeName", appInfo.getMyTrainee().getUsername());
		model.addAttribute("traineeId",appInfo.getMyTrainee().getId());
		model.addAttribute("trainerId",appInfo.getMyTrainer().getId());
		model.addAttribute("weather", weatherReport.toString());
		return "/calendar/appointments/appView";
	}
	
	//this method updates the status of an appointment in multiple ways
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{id}/update")
	public String updateAppointment(
			@AuthenticationPrincipal User user,
			Authentication authentication,
			Model model,
			@PathVariable Integer id,
			@RequestParam String action,
	        RedirectAttributes redirectAttributes) {
		
		
		AppointmentValidationResult result = appService.validateRequest(user, authentication, id);
		if (!result.isSuccess()) {
			model.addAttribute("errorReason",result.getReason());
			return "/error";
		}
		
		AppointmentValidationInfo info = result.getInfo();
		Appointment app = info.getMyApp();
		
		//make sure appointment is up to date
		appService.syncAppointmentStatus(app);
		
		
		//change appointment status
		if (info.isTrainer()) {
			switch (action) {
				case "confirm":
					if (app.getStatus().equals(AppointmentStatus.Requested)) {
						app.setStatus(AppointmentStatus.Accepted);
						
						//when a trainer confirms an appointment it should also automaticaly reject other appointments on this availability
						List<Appointment> conflictingApps = appService.getOverlappingAppointmentsByTrainerAndTime(info.getMyTrainer().getId(), app.getStartTime(), app.getEndTime());
						for (Appointment a: conflictingApps) {
							if (!a.equals(app)) {
								if (a.getStatus().equals(AppointmentStatus.Requested)) {
									a.setStatus(AppointmentStatus.Rejected);
								}
							}
						}
					}else {
						model.addAttribute("errorReason","Invalid form input");
						return "/error";
					}
					break;
				case "reject":
					if (app.getStatus().equals(AppointmentStatus.Requested)) {
						app.setStatus(AppointmentStatus.Rejected);
					}else {
						model.addAttribute("errorReason","Invalid form input");
						return "/error";
					}
					break;
				case "cancel":
					if (app.getStatus().equals(AppointmentStatus.Accepted)) {
						app.setStatus(AppointmentStatus.Canceled);
					}else {
						model.addAttribute("errorReason","Invalid form input");
						return "/error";
					}
					break;
				default:
					model.addAttribute("errorReason","Invalid form input");
					return "/error";
			}
		}else {
			switch (action) {
				case "cancel":
					if (app.getStatus().equals(AppointmentStatus.Accepted) || app.getStatus().equals(AppointmentStatus.Requested)) {
						app.setStatus(AppointmentStatus.Canceled);
					}else {
						model.addAttribute("errorReason","Invalid form input");
						return "/error";
					}
					break;
				default:
					model.addAttribute("errorReason","Invalid form input");
					return "/error";
			}
		}
		
		
		try {
			appService.saveAppointment(app);
		}catch (Exception e) {
			model.addAttribute("errorReason","There was a problem with updating the appointment");
			return "/error";
		}
		
		return "redirect:/appointments/" + id + "/view";
		
	}
	
	
	//edit the log of an appointment
	@PreAuthorize("hasRole('ROLE_TRAINER')")
	@GetMapping("/{id}/edit")
	public String editAppointment(@AuthenticationPrincipal User user, Authentication authentication, Model model, @PathVariable Integer id) {
		AppointmentValidationResult result = appService.validateRequest(user, authentication, id);
		if (!result.isSuccess()) {
			model.addAttribute("errorReason",result.getReason());
			return "/error";
		}
		AppointmentValidationInfo info = result.getInfo();
		Appointment app = info.getMyApp();
		appService.syncAppointmentStatus(app);
		
		//we can only edit the log for a COMPLETED appointment
		if (!app.getStatus().equals(AppointmentStatus.Completed)) {
			model.addAttribute("errorReason","You cannot edit the log of an appointment that has not been completed");
			return "/error";
		}
		
		model.addAttribute("text", app.getLog());
		model.addAttribute("appId",id);
		return "/calendar/appointments/appEditLog";
	}
	
	@PreAuthorize("hasRole('ROLE_TRAINER')")
	@PostMapping("/{id}/edit")
	public String editAppointmentPost(
			@AuthenticationPrincipal User user,
			Authentication authentication,
			Model model,
			@PathVariable Integer id,
			@RequestParam String text) {
		
		AppointmentValidationResult result = appService.validateRequest(user, authentication, id);
		if (!result.isSuccess()) {
			model.addAttribute("errorReason",result.getReason());
			return "/error";
		}
		AppointmentValidationInfo info = result.getInfo();
		Appointment app = info.getMyApp();
		appService.syncAppointmentStatus(app);
		
		if (!app.getStatus().equals(AppointmentStatus.Completed)) {
			model.addAttribute("errorReason","You cannot edit the log of an appointment that has not been completed");
			return "/error";
		}
		
		app.setLog(text);
		
		try {
			appService.saveAppointment(app);
		}catch (Exception e) {
			model.addAttribute("errorReason","There was a problem with updating the appointment");
			return "/error";
		}
		
		return "redirect:/appointments/" + id + "/view";
	}
	
	
	
}

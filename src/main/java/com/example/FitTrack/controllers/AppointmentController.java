package com.example.FitTrack.controllers;

import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.FitTrack.dto.validation.AppointmentValidationInfo;
import com.example.FitTrack.dto.validation.AppointmentValidationResult;
import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.entities.UserRole;
import com.example.FitTrack.enums.AppointmentStatus;
import com.example.FitTrack.service.AppointmentService;
import com.example.FitTrack.service.AvailabilityService;
import com.example.FitTrack.service.SiteUserService;
import com.example.FitTrack.service.UserRoleService;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

	
	private SiteUserService userService;
	private UserRoleService roleService;
	private AppointmentService appService;
	private AvailabilityService availService;
	
	
	public AppointmentController(SiteUserService userService, UserRoleService roleService, AppointmentService appService, AvailabilityService availService) {
		this.userService = userService;
		this.roleService = roleService;
		this.appService = appService;
		this.availService = availService;
	}
	
	
	
	
	@PreAuthorize("isAuthenticated()")  //different behavior depending on whether a trainer or a trainee wants to view an appointment
	@GetMapping("/{id}/view")
	public String viewAppointment(@AuthenticationPrincipal User user, Model model, Authentication authentication, @PathVariable Integer id) {
		AppointmentValidationResult result = appService.validateRequest(user, authentication, id);
		if (!result.isSuccess()) {
			model.addAttribute("errorReason",result.getReason());
			return "/error";
		}
		
		//checks passed, show appointment
		AppointmentValidationInfo appInfo = result.getInfo();
		
		model.addAttribute("appointment", appInfo.getMyApp());
		model.addAttribute("AppointmentStatus", AppointmentStatus.class);
		model.addAttribute("trainerName", appInfo.getMyTrainer().getUsername());
		model.addAttribute("traineeName", appInfo.getMyTrainee().getUsername());
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
		appService.updateAppointment(app);
		
		
		//change appointment status
		if (info.isTrainer()) {
			switch (action) {
				case "confirm":
					if (app.getStatus().equals(AppointmentStatus.Requested)) {
						app.setStatus(AppointmentStatus.Accepted);
						//when a trainer confirms an appointment it should also automaticaly reject other appointments on this availability
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
		appService.updateAppointment(app);
		
		//we can only edit the log for a completed appointment
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
		appService.updateAppointment(app);
		
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

package com.example.FitTrack.controllers;

import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.FitTrack.dto.AvailabilityFormDto;
import com.example.FitTrack.entities.Availability;
import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.service.AppointmentService;
import com.example.FitTrack.service.AvailabilityService;
import com.example.FitTrack.service.SiteUserService;
import com.example.FitTrack.service.UserRoleService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/availabilities")
public class AvailabilityController {

	private SiteUserService userService;
	private UserRoleService roleService;
	private AppointmentService appService;
	private AvailabilityService availService;
	
	
	public AvailabilityController(SiteUserService userService, UserRoleService roleService, AppointmentService appService, AvailabilityService availService) {
		this.userService = userService;
		this.roleService = roleService;
		this.appService = appService;
		this.availService = availService;
	}
	
	@PreAuthorize("hasRole('ROLE_TRAINER')")  //only trainers can view and edit their avails
	@GetMapping("/{id}/view")
	public String viewAvailability(@AuthenticationPrincipal User user, Model model, @PathVariable Integer id) {
		//check user
		Optional<SiteUser> givenTrainer = userService.getUserByUsername(user.getUsername());
		if (givenTrainer.isEmpty()) {
			model.addAttribute("errorReason","user does not exist, this should never be called as we are already logged in as the user that supposedly doesnt exist");
			return "/error";
		}
		SiteUser confirmedTrainer = givenTrainer.get();
		
		//check if avail exists
		Optional<Availability> givenAvail = availService.getAvailById(id);
		if (givenAvail.isEmpty()) {
			model.addAttribute("errorReason","The requested availability does not exist");
			return "/error";
		}
		Availability confirmedAvail = givenAvail.get();
		
		//check if avail belongs to current user
		if (!confirmedAvail.getMyTrainer().equals(confirmedTrainer)) {
			model.addAttribute("errorReason","You do not have access to this availability");
			return "/error";
		}
		
		
		//checks passed, display results
		model.addAttribute("availability",confirmedAvail);
		return "/calendar/availabilities/availView";
	}
	
	@PreAuthorize("hasRole('ROLE_TRAINER')")
	@PostMapping("/{id}/delete")
	public String deleteAvailability(@AuthenticationPrincipal User user, Model model, @PathVariable Integer id) {
		//repeat all the checks again, we could put these checks in their own helper method but then we would also
		//have to create a helper class to keep the error message or the successful info (like we did with the schedule controller)
		//so this copy paste is simpler
		
		//check user
		Optional<SiteUser> givenTrainer = userService.getUserByUsername(user.getUsername());
		if (givenTrainer.isEmpty()) {
			model.addAttribute("errorReason","user does not exist, this should never be called as we are already logged in as the user that supposedly doesnt exist");
			return "/error";
		}
		SiteUser confirmedTrainer = givenTrainer.get();
		
		//check if avail exists
		Optional<Availability> givenAvail = availService.getAvailById(id);
		if (givenAvail.isEmpty()) {
			model.addAttribute("errorReason","The requested availability does not exist");
			return "/error";
		}
		Availability confirmedAvail = givenAvail.get();
		
		//check if avail belongs to current user
		if (!confirmedAvail.getMyTrainer().equals(confirmedTrainer)) {
			model.addAttribute("errorReason","You do not have access to this availability");
			return "/error";
		}
		
		//checks end here, new code starts here
		try {
			availService.deleteAvailability(confirmedAvail);
			return "redirect:/availabilities/deleteSuccess";
		}catch (Exception e) {
			model.addAttribute("errorReason","There was a problem with deleting the availability");
			return "/error";
		}
	}
	
	@PreAuthorize("hasRole('ROLE_TRAINER')")
	@GetMapping("/deleteSuccess")
	public String successfullyDeleted(Model model) {
		return "/calendar/availabilities/deleteAvailSuccess";
	}
	
	
	
	@PreAuthorize("hasRole('ROLE_TRAINER')")
	@GetMapping("/create")
	public String availabilityCreateGet(Model model) {
		model.addAttribute("availForm", new AvailabilityFormDto());
		return "/calendar/availabilities/createForm";
	}
	
	@PreAuthorize("hasRole('ROLE_TRAINER')")
	@PostMapping("/create")
	public String availabilityCreatePost(
			Model model,
			@Valid @ModelAttribute("availForm") AvailabilityFormDto form,
			BindingResult bindingResult,
			@AuthenticationPrincipal User user) {
		Optional<SiteUser> givenTrainer = userService.getUserByUsername(user.getUsername());
		if (givenTrainer.isEmpty()) {
			model.addAttribute("errorReason","user does not exist, this should never be called as we are already logged in as the user that supposedly doesnt exist");
			return "/error";
		}
		SiteUser confirmedTrainer = givenTrainer.get();
		
		
		if (bindingResult.hasErrors()) {
			return "/calendar/availabilities/createForm";
		}
		
		
		
		try {
			availService.createAvailabilityFromForm(form,confirmedTrainer);
	        return "redirect:/calendar";
		} catch (Exception e) {
			bindingResult.reject(
	                "save.failed",
	                "Something went wrong while saving. Please try again."
	        );
	        return "/calendar/availabilities/createForm";
		}
	}
	
	
}

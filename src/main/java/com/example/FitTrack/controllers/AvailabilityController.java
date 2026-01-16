package com.example.FitTrack.controllers;

import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
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
import com.example.FitTrack.dto.validation.AvailabilityValidationInfo;
import com.example.FitTrack.dto.validation.AvailabilityValidationResult;
import com.example.FitTrack.entities.Availability;
import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.service.AvailabilityService;
import com.example.FitTrack.service.SiteUserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/availabilities")
public class AvailabilityController {

	private SiteUserService userService;
	private AvailabilityService availService;
	
	
	public AvailabilityController(SiteUserService userService, AvailabilityService availService) {
		this.userService = userService;
		this.availService = availService;
	}
	
	
	
	
	@PreAuthorize("hasRole('ROLE_TRAINER')")  //only trainers can view and edit their avails (a trainee clicking on an availability will be taken to the schedule appointment page)
	@GetMapping("/{id}/view")
	public String viewAvailability(@AuthenticationPrincipal User user, Model model, @PathVariable Integer id) {
		
		AvailabilityValidationResult result = verifyRequest(user, id);
		if (!result.isSuccess()) {
			model.addAttribute("errorReason",result.getReason());
			return "/error";
		}
		AvailabilityValidationInfo info = result.getInfo();
		
		
		//checks passed, display results
		model.addAttribute("availability",info.getConfirmedAvailability());  //there is no need to pass a dto as we will display all availability attributes (id, trainer, time info)
		return "/calendar/availabilities/availView";
	}
	
	@PreAuthorize("hasRole('ROLE_TRAINER')")
	@PostMapping("/{id}/delete")
	public String deleteAvailability(@AuthenticationPrincipal User user, Model model, @PathVariable Integer id) {
		
		AvailabilityValidationResult result = verifyRequest(user, id);
		if (!result.isSuccess()) {
			model.addAttribute("errorReason",result.getReason());
			return "/error";
		}
		AvailabilityValidationInfo info = result.getInfo();
		
		//checks passed, try deleting
		try {
			availService.deleteAvailability(info.getConfirmedAvailability());
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
	
	
	
	
	
	
	
	//helper method
	
	
	
	/*
	in this method we will verify that the requesting trainer can view this availability
	we are using the:
		AvailabilityValidationResult
		AvailabilityValidationInfo
	classes, which were made for the schedule controller but can be used here as well
	some of their attributes will be left null as they are not needed here
	*/
	private AvailabilityValidationResult verifyRequest(User user, Integer id) {
		//check user (we know they are a trainer from the authorization)
		Optional<SiteUser> givenTrainer = userService.getUserByUsername(user.getUsername());
		if (givenTrainer.isEmpty()) {
			System.out.println("user does not exist, this should never be called as we are already logged in as the user that supposedly doesnt exist");
			return new AvailabilityValidationResult(false, "Current user does not exist", null);
		}
		SiteUser confirmedTrainer = givenTrainer.get();
		
		//check if avail exists
		Optional<Availability> givenAvail = availService.getAvailById(id);
		if (givenAvail.isEmpty()) {
			return new AvailabilityValidationResult(false, "The requested availability does not exist", null);
		}
		Availability confirmedAvail = givenAvail.get();
		
		//check if avail belongs to current user
		if (!confirmedAvail.getMyTrainer().equals(confirmedTrainer)) {
			return new AvailabilityValidationResult(false, "You do not have access to this availability", null);
		}
		
		//checks passed, return availability
		AvailabilityValidationInfo info = new AvailabilityValidationInfo(
				confirmedTrainer,
				null,
				confirmedAvail,
				null,
				null,
				null
				);
		
		return new AvailabilityValidationResult(true, null, info);
	}
	
}

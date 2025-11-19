package com.example.FitTrack.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.FitTrack.enums.UserRole;
import com.example.FitTrack.service.SiteUserService;

@Controller
@RequestMapping("/Trainers")
public class TrainerController {

	private SiteUserService userService;

	public TrainerController(SiteUserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("")
	public String showAllTrainers(Model model) {
		model.addAttribute("trainer_list",userService.getUsersByRole(UserRole.Trainer));
		return "Trainers/TrainerList";
	}
	
	
}

package com.example.FitTrack.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.FitTrack.service.SiteUserService;
import com.example.FitTrack.service.UserRoleService;

@Controller
@RequestMapping("/Trainers")
public class TrainerController {

	private SiteUserService userService;
	private UserRoleService roleService;

	public TrainerController(SiteUserService userService, UserRoleService roleService) {
		this.userService = userService;
		this.roleService = roleService;
	}
	
	@GetMapping("")
	public String showAllTrainers(Model model) {
		model.addAttribute("trainer_list",userService.getUsersByRole(roleService.getRoleByName("TRAINER")));
		return "Trainers/TrainerList";
	}
	
	
}

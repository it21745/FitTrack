package com.example.FitTrack.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.FitTrack.dto.UserDto;
import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.service.SiteUserService;
import com.example.FitTrack.service.UserRoleService;


@Controller
@RequestMapping("/trainers")
public class TrainerController {

	private SiteUserService userService;
	private UserRoleService roleService;
	

	public TrainerController(SiteUserService userService, UserRoleService roleService) {
		this.userService = userService;
		this.roleService = roleService;
	}
	
	@GetMapping("")
	public String showAllTrainers(Model model) {
		List<SiteUser> trainers = userService.getUsersByRole(roleService.getRoleByName("ROLE_TRAINER")).get();
		List<UserDto> userDtos = new ArrayList<>();
		for (SiteUser t: trainers) {
			UserDto curDto = new UserDto();
			curDto.setId(t.getId());
			curDto.setUsername(t.getUsername());
			userDtos.add(curDto);
		}
		
		
		model.addAttribute("trainer_list",userDtos);
		return "trainers/trainerList";
	}
	

}

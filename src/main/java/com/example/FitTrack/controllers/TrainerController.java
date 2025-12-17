package com.example.FitTrack.controllers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.FitTrack.dto.EventDto;
import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.entities.Availability;
import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.entities.UserRole;
import com.example.FitTrack.enums.AppointmentStatus;
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
		model.addAttribute("trainer_list",userService.getUsersByRole(roleService.getRoleByName("ROLE_TRAINER")).get());
		return "trainers/trainerList";
	}
}

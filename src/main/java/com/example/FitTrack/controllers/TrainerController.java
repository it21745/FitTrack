package com.example.FitTrack.controllers;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
import com.example.FitTrack.dto.UserDto;
import com.example.FitTrack.dto.API_dto.ForecastBlock;
import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.entities.Availability;
import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.entities.UserRole;
import com.example.FitTrack.enums.AppointmentStatus;
import com.example.FitTrack.service.SiteUserService;
import com.example.FitTrack.service.UserRoleService;
import com.example.FitTrack.service.WeatherService;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/trainers")
public class TrainerController {

	private SiteUserService userService;
	private UserRoleService roleService;
	private WeatherService weatherService;
	

	public TrainerController(SiteUserService userService, UserRoleService roleService, WeatherService weatherService) {
		this.userService = userService;
		this.roleService = roleService;
		this.weatherService = weatherService;
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
	
	@GetMapping("/weather")
	public String weatherTest(Model model) {
		
		ForecastBlock out = weatherService.getAthensWeatherAtInstant(Instant.now().plus(2,ChronoUnit.DAYS)).block();
		if (out != null) {
			//System.out.println(out.str());
		}else {
			System.out.println("i got a null");
		}
		
		
		return "/temp/1";
	}
}

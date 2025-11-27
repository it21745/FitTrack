package com.example.FitTrack.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.FitTrack.entities.UserRole;
import com.example.FitTrack.repository.UserRoleRepository;

import jakarta.annotation.PostConstruct;

@Controller
@RequestMapping("/login")
public class LoginController {

	UserRoleRepository roleRepo;
	
	public LoginController(UserRoleRepository roleRepo) {
		this.roleRepo = roleRepo;
	}

	//αρχικοποιουμε εδω τους 2 ρολους που θα εχουμε
	@PostConstruct
	public void setup() {
		UserRole role_trainer = new UserRole("ROLE_TRAINER");
		UserRole role_trainee = new UserRole("ROLE_TRAINEE");
		
		roleRepo.updateOrInsert(role_trainer);
		roleRepo.updateOrInsert(role_trainee);
	}
	
	
	@GetMapping("")
	public String login() {
		return "auth/login";
	}
}

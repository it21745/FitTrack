package com.example.FitTrack.controllers.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.FitTrack.dto.API_dto.trainers.TrainerDto;
import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.entities.UserRole;
import com.example.FitTrack.service.SiteUserService;
import com.example.FitTrack.service.UserRoleService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/trainers")
public class TrainersRestController {
	private final SiteUserService userService;
	private final UserRoleService roleService;
	
	public TrainersRestController(SiteUserService userService, UserRoleService roleService) {
		this.userService = userService;
		this.roleService = roleService;
	}
	
	
	@Operation(
    		summary = "Return trainer list",
    	    description = "Returns a list of all trainers"
    )
	@GetMapping("")
	public ResponseEntity<List<TrainerDto>> getAllTrainers(){
		UserRole roleTrainer = roleService.getRoleByName("ROLE_TRAINER");
		Optional<List<SiteUser>> givenTrainers = userService.getUsersByRole(roleTrainer);
		if (givenTrainers.isEmpty()) {
			//empty response
			return ResponseEntity.ok().build();
		}
		List<SiteUser> confirmedTrainers = givenTrainers.get();
		
		//create dto list
		List<TrainerDto> returnList = new ArrayList<>();
		for (SiteUser u: confirmedTrainers) {
			returnList.add(TrainerDto.TrainerToDto(u));
		}
		return ResponseEntity.ok(returnList);
	}
}

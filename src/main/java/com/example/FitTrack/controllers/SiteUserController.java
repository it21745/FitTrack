package com.example.FitTrack.controllers;

import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.entities.UserRole;
import com.example.FitTrack.service.SiteUserService;
import com.example.FitTrack.service.UserRoleService;

import com.example.FitTrack.dto.FitnessProfile;
import com.example.FitTrack.dto.UserDto;
import com.example.FitTrack.util.JsonUtils;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/users")
public class SiteUserController {

    private final SiteUserService userService;
    private final UserRoleService roleService;

    public SiteUserController(SiteUserService userService, UserRoleService roleService) {
        this.userService = userService;
		this.roleService = roleService;
    }
    
    //register methods
    @GetMapping("/register")
    public String register(Model model) {
    	SiteUser user = new SiteUser();
    	model.addAttribute("user",user);
    	
    	List<UserRole> roles = roleService.getAllRoles();
    	model.addAttribute("roles",roles);
    	
    	return "auth/registerForm";
    }

    @PostMapping("/save")
    public String saveUser(
    		@Valid @ModelAttribute("user") SiteUser user,
    		BindingResult theBindingResult,
    		@RequestParam(name = "roleId", required = false) Integer roleId,
    		Model model) {
    	
    	if (theBindingResult.hasErrors()) {
        	
        	List<UserRole> roles = roleService.getAllRoles();
        	model.addAttribute("roles",roles);
        	return "auth/registerForm";
        }else {
        	userService.saveUser(user, roleId);
        	return "redirect:/";
        }
    }
    
    
    
    //profile viewing methods
    
    
    @GetMapping("/profile")
    public String viewMyProfile(Model model, Principal principal) {

        String username = principal.getName();
        SiteUser user = userService.findByUsername(username);

        // Μετατροπή JSON -> FitnessProfile
        FitnessProfile fp = JsonUtils.fromJson(user.getFitnessProfileJson());


        
        model.addAttribute("user", user);
        model.addAttribute("fitnessProfile", fp);

        return "users/userProfile";
    }

    @GetMapping("/profile/edit")
    public String editMyProfile(Model model, Principal principal) {

        String username = principal.getName();
        SiteUser user = userService.findByUsername(username);

        // Μετατροπή JSON -> FitnessProfile object
        FitnessProfile fp = JsonUtils.fromJson(user.getFitnessProfileJson());

        model.addAttribute("user", user);
        model.addAttribute("fitnessProfile", fp);

        return "users/editUserProfile";
    }

    @PostMapping("/profile/update")
    public String updateMyProfile(
            @ModelAttribute("user") SiteUser formUser,
            @ModelAttribute("fitnessProfile") FitnessProfile fp,
            Principal principal) {

        String username = principal.getName();
        SiteUser user = userService.findByUsername(username);

        // Basic info
        user.setFirstName(formUser.getFirstName());
        user.setLastName(formUser.getLastName());
        user.setInfo(formUser.getInfo());

        // Save JSON
        user.setFitnessProfileJson(JsonUtils.toJson(fp));

        userService.updateUser(user);

        return "redirect:/users/profile";
    }
    
    
    @GetMapping("view/{id}")
    public String viewPublicProfile(Model model, @PathVariable Integer id) {
    	Optional<SiteUser> givenUser = userService.getUserById(id);
    	if (givenUser.isEmpty()) {
    		model.addAttribute("errorReason","user does not exist");
    		return "/error";
		}
		SiteUser confirmedUser = givenUser.get();
		
		// Μετατροπή JSON -> FitnessProfile
        FitnessProfile fp = JsonUtils.fromJson(confirmedUser.getFitnessProfileJson());
		
		
		UserDto userDto = new UserDto(
				confirmedUser.getId(),
				confirmedUser.getUsername(),
				confirmedUser.getEmail(),
				confirmedUser.getFirstName(),
				confirmedUser.getLastName(),
				confirmedUser.getRoleString(),
				confirmedUser.getInfo()
				);
		
		model.addAttribute("user", userDto);
        model.addAttribute("fitnessProfile", fp);
    	
        return "users/userProfilePublic";
    }
}

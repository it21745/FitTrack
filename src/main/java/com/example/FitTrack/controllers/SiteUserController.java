package com.example.FitTrack.controllers;

import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.entities.UserRole;
import com.example.FitTrack.service.SiteUserService;
import com.example.FitTrack.service.UserRoleService;

import jakarta.validation.Valid;

import java.util.List;
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

//    @GetMapping
//    public String listUsers(Model model) {
//        model.addAttribute("users", userService.getAllUsers());
//        return "users/list";
//    }
    
    @GetMapping("/register")
    public String register(Model model) {
    	SiteUser user = new SiteUser();
    	model.addAttribute("user",user);
    	
    	List<UserRole> roles = roleService.getAllRoles();
    	model.addAttribute("roles",roles);
    	
    	return "auth/registerForm";
    }

//    @GetMapping("/new")
//    public String showCreateForm(Model model) {
//        model.addAttribute("user", new SiteUser());
//        model.addAttribute("roles", new UserRole());
//        return "users/form";
//    }

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

    @GetMapping("/profile")
    public String viewMyProfile(Model model, Principal principal) {

        String username = principal.getName();              // παίρνουμε τον logged-in user
        SiteUser user = userService.findByUsername(username);

        model.addAttribute("user", user);
        return "users/userProfile";
    }

    @GetMapping("/profile/edit")
    public String editMyProfile(Model model, Principal principal) {

        String username = principal.getName();
        SiteUser user = userService.findByUsername(username);

        model.addAttribute("user", user);
        return "users/editUserProfile";
    }

    @PostMapping("/profile/update")
    public String updateMyProfile(@ModelAttribute("user") SiteUser formUser, Principal principal) {

        String username = principal.getName();
        SiteUser user = userService.findByUsername(username);

        // Μόνο αυτά επιτρέπεται να αλλάξει ο χρήστης
        user.setFirstName(formUser.getFirstName());
        user.setLastName(formUser.getLastName());
        user.setInfo(formUser.getInfo());

        userService.updateUser(user);

        return "redirect:/users/profile";
    }
}

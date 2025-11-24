package com.example.FitTrack.controllers;

import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.enums.UserRole;
import com.example.FitTrack.service.SiteUserService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class SiteUserController {

    private final SiteUserService userService;

    public SiteUserController(SiteUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new SiteUser());
        model.addAttribute("roles", UserRole.values());
        return "users/form";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") SiteUser user, BindingResult theBindingResult) {
        if (theBindingResult.hasErrors()) {
        	System.out.println("Error, bad input for user");
        	return "users/form";
        }else {
        	userService.saveUser(user);
            return "redirect:/users";
        }
    }
}

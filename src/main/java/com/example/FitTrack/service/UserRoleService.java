package com.example.FitTrack.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.FitTrack.entities.UserRole;
import com.example.FitTrack.repository.UserRoleRepository;

import jakarta.transaction.Transactional;

@Service
public class UserRoleService {
	private UserRoleRepository repo;

	public UserRoleService(UserRoleRepository repo) {
		this.repo = repo;
	}
	
	//methods
	
	@Transactional
	public List<UserRole> getAllRoles(){
		return repo.findAll();
	}
	
	@Transactional
	public UserRole getRoleByName(String name) {
		return repo.findByName(name);
	}
	
//	public UserRole getRoleOfUser() {
//		
//	}
	
}

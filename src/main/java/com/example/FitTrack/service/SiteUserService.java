package com.example.FitTrack.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.FitTrack.repository.SiteUserRepository;

import jakarta.transaction.Transactional;

import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.enums.UserRole;

@Service
public class SiteUserService {

	private SiteUserRepository repo;

	
	public SiteUserService(SiteUserRepository repo) {
		this.repo = repo;
	}
	
	//methods
	
	@Transactional
	public List<SiteUser> getAllUsers(){
		return repo.findAll();
	}
	
	@Transactional
	public List<SiteUser> getUsersByRole(UserRole role){
		return repo.findByRole(role);
	}
	
	@Transactional
	public SiteUser getUserById(Integer id) {
		return repo.findById(id).get();
	}
	
	@Transactional
	public SiteUser getUserByUsername(String username) {
		return repo.findByUsername(username);
	}

	@Transactional
	public void saveUser(SiteUser user) {
		repo.save(user);
	}
	
	
	
	
}

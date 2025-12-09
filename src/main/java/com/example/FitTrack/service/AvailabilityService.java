package com.example.FitTrack.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.FitTrack.repository.AvailabilityRepository;

import jakarta.transaction.Transactional;
import com.example.FitTrack.entities.Availability;

@Service
public class AvailabilityService {

	private AvailabilityRepository repo;

	public AvailabilityService(AvailabilityRepository repo) {
		this.repo = repo;
	}
	
	//methods
	
	@Transactional
	public List<Availability> getAllAvailabilities(){
		return repo.findAll();
	}
	
	@Transactional
	public Optional<Availability> getAvailById(int id){
		return repo.findById(id);
	}
	
	
}

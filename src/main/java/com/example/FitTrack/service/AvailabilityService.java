package com.example.FitTrack.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.FitTrack.repository.AvailabilityRepository;
import com.example.FitTrack.repository.SiteUserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import com.example.FitTrack.dto.AvailabilityFormDto;
import com.example.FitTrack.entities.Availability;
import com.example.FitTrack.entities.SiteUser;

@Service
public class AvailabilityService {

	private AvailabilityRepository repo;
	private SiteUserRepository userRepo;

	public AvailabilityService(AvailabilityRepository repo, SiteUserRepository userRepo) {
		this.repo = repo;
		this.userRepo = userRepo;
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
	
	public Optional<List<Availability>> getAvailByUserId(int id){
		SiteUser user = userRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		
		//is a trainer (only trainers have availabilities)
		boolean hasTrainerRole = user.getRoles().stream()
		        .anyMatch(role -> "ROLE_TRAINER".equals(role.getName()));
		if (!hasTrainerRole) {
	        throw new IllegalStateException("User is not a trainer");
	    }
		
		//get availabilities
		List<Availability> avails = repo.findByMyTrainerId(id);
		if (avails.isEmpty()) {
			return Optional.empty();
		}
		
		return Optional.of(avails);
			
	}
	
	@Transactional
	public void deleteAvailability(Availability avail) {
		repo.delete(avail);
	}
	
	@Transactional
	public void createAvailabilityFromForm(AvailabilityFormDto form, SiteUser trainer) {
		Availability avail = new Availability();
		avail.setMyTrainer(trainer);
		avail.setOneTime(form.isOneTime());
		avail.setDate(form.getDate());
		avail.setDay(form.getDay());
		avail.setStartTime(form.getStartTime());
		avail.setEndTime(form.getStartTime().plusMinutes(form.getDurationMinutes()));
		
		repo.save(avail);
		
	}
	
	
}

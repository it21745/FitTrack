package com.example.FitTrack.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.FitTrack.repository.AvailabilityRepository;

import jakarta.transaction.Transactional;

import com.example.FitTrack.dto.AvailabilityFormDto;
import com.example.FitTrack.entities.Availability;
import com.example.FitTrack.entities.SiteUser;

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

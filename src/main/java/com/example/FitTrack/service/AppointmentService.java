package com.example.FitTrack.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.example.FitTrack.repository.AppointmentRepository;

import jakarta.transaction.Transactional;
import com.example.FitTrack.entities.Appointment;

@Service
public class AppointmentService {

	private AppointmentRepository repo;

	public AppointmentService(AppointmentRepository repo) {
		this.repo = repo;
	}
	
	//methods
	
	@Transactional
	public List<Appointment> getAllAppointments(){
		return repo.findAll();
	}
	
	
}

package com.example.FitTrack.service;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.FitTrack.repository.AppointmentRepository;
import com.example.FitTrack.repository.SiteUserRepository;

import jakarta.transaction.Transactional;
import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.enums.AppointmentStatus;

@Service
public class AppointmentService {

	private AppointmentRepository repo;
	private SiteUserRepository userRepo;

	public AppointmentService(AppointmentRepository repo) {
		this.repo = repo;
	}
	
	//methods
	
	@Transactional
	public List<Appointment> getAllAppointments(){
		return repo.findAll();
	}
	
	@Transactional
	public List<Appointment> getOverlappingAppointmentsByTrainerAndTime(Integer trainerId, Instant start, Instant end){
		if (trainerId == null || start == null || end == null) {
			throw new IllegalArgumentException("Input values must not be null");
		}
		if (start.isAfter(end)) {
			throw new IllegalArgumentException("Start time can't be after end time");
		}
		return repo.findOverlappingAppointmentsTrainer(trainerId, start, end);
	}
	
	@Transactional
	public List<Appointment> getOverlappingAppointmentsByTraineeAndTime(Integer traineeId, Instant start, Instant end){
		if (traineeId == null || start == null || end == null) {
			throw new IllegalArgumentException("Input values must not be null");
		}
		if (start.isAfter(end)) {
			throw new IllegalArgumentException("Start time can't be after end time");
		}
		return repo.findOverlappingAppointmentsTrainee(traineeId, start, end);
	}

	@Transactional
	public Appointment saveAppointment(Appointment app) {
		//there is a logic in the scheduleController that checks for conflicting appointments and then calls this
		//if i have time i will move that logic into this method, it needs to be in a transactional method to make sure it works
		return repo.save(app);
	}
	
	
}

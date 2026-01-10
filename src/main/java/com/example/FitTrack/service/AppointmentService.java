package com.example.FitTrack.service;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.FitTrack.repository.AppointmentRepository;
import com.example.FitTrack.repository.SiteUserRepository;
import com.example.FitTrack.dto.validation.AppointmentValidationInfo;
import com.example.FitTrack.dto.validation.AppointmentValidationResult;
import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.enums.AppointmentStatus;

import com.example.FitTrack.dto.appointment.AppointmentRequestDto;
import com.example.FitTrack.dto.appointment.AppointmentResponseDto;
import com.example.FitTrack.dto.WeatherReportDto;
import com.example.FitTrack.dto.appointment.AppointmentDetailsDto;

import java.time.ZoneId;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class AppointmentService {

	private AppointmentRepository repo;
	private SiteUserRepository userRepo;
	private WeatherService weatherService;

	public AppointmentService(AppointmentRepository repo, SiteUserRepository userRepo, WeatherService weatherService) {
		this.repo = repo;
		this.userRepo = userRepo;
		this.weatherService = weatherService;
	}
	
	//methods
	
	@Transactional
	public List<Appointment> getAllAppointments(){
		return repo.findAll();
	}
	
	@Transactional
	public Optional<Appointment> getAppById(int id){
		return repo.findById(id);
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
	
	
	
	//a method to validate that any request
	//a. is of an existing appointment
	//and b. the appointment belongs to the requesting user
	@Transactional
	public AppointmentValidationResult validateRequest(User user, Authentication authentication, Integer id) {
		//check user
		Optional<SiteUser> givenUser = userRepo.findByUsername(user.getUsername());
		if (givenUser.isEmpty()) {
			return new AppointmentValidationResult(false, "user does not exist, this should never be called as we are already logged in as the user that supposedly doesnt exist", null);
		}
		SiteUser confirmedUser = givenUser.get();
		
		//check if appointment exists
		Optional<Appointment> givenApp = repo.findById(id);
		if (givenApp.isEmpty()) {
			return new AppointmentValidationResult(false, "The requested appointment does not exist", null);
		}
		Appointment confirmedApp = givenApp.get();
		
		//check role of requesting user
		boolean isTrainer = authentication.getAuthorities().stream()
	            .anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER"));
		
		if (isTrainer) {
			if (!confirmedApp.getMyTrainer().equals(confirmedUser)) {
				return new AppointmentValidationResult(false, "You are trying to access an appointment you are not the trainer for", null);
			}
			
		}else {
			if (!confirmedApp.getMyTrainee().equals(confirmedUser)) {
				return new AppointmentValidationResult(false, "You are trying to access an appointment you are not the trainee for", null);
			}
			
		}
		
		AppointmentValidationInfo resultInfo = new AppointmentValidationInfo();
		resultInfo.setMyApp(confirmedApp);
		resultInfo.setMyTrainer(confirmedApp.getMyTrainer());
		resultInfo.setMyTrainee(confirmedApp.getMyTrainee());
		resultInfo.setIsTrainer(isTrainer);
		
		return new AppointmentValidationResult(true, null, resultInfo);
		
	}
	
	
	@Transactional
    public void syncAppointmentStatus(Appointment app) {
    	//it just takes in an appointment and makes sure it's up to date
    	//this method is used by other methods that want to sync an appointment as a safety measure before updating it
    	Instant now = Instant.now();
    	if (app.getEndTime().isBefore(now)) {
    		if (app.getStatus().equals(AppointmentStatus.Accepted)) {
    			app.setStatus(AppointmentStatus.Completed);
    		}else if (app.getStatus().equals(AppointmentStatus.Requested)) {
    			app.setStatus(AppointmentStatus.Rejected);
    		}
    	}
    }

    @Transactional
    public AppointmentResponseDto create(AppointmentRequestDto dto) {


        SiteUser trainee = userRepo.findByUsername(
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        ).orElseThrow(() -> new RuntimeException("Logged in user not found"));


        SiteUser trainer = userRepo.findById(dto.getTrainerId().intValue())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));


        Appointment app = new Appointment();
        app.setMyTrainee(trainee);
        app.setMyTrainer(trainer);
        app.setStartTime(
                dto.getAppointmentDate()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
        app.setStatus(AppointmentStatus.Requested);


        Appointment saved = saveAppointment(app);

        return toResponseDto(saved);
    }

    @Transactional
    public List<AppointmentResponseDto> getUserAppointments() {

        SiteUser user = userRepo.findByUsername(
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        )
        .orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Trainee not found"
                )
        );

        List<Appointment> apps = repo.findByMyTrainee(user);

        List<AppointmentResponseDto> result = new ArrayList<>();
        for (Appointment app : apps) {
            result.add(toResponseDto(app));
        }
        return result;
    }

    @Transactional
    public List<AppointmentResponseDto> getTrainerAppointments() {

        SiteUser trainer = userRepo.findByUsername(
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        )
        		.orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Trainer not found"
                )
        );

        List<Appointment> apps = repo.findByMyTrainer(trainer);

        List<AppointmentResponseDto> result = new ArrayList<>();
        for (Appointment app : apps) {
            result.add(toResponseDto(app));
        }
        return result;
    }

    @Transactional
    public AppointmentDetailsDto getAppointmentDetails(Long id) {

        Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication();

        User user = (User) auth.getPrincipal();

        AppointmentValidationResult validation =
                validateRequest(user, auth, id.intValue());

        if (!validation.isSuccess()) {
            throw new RuntimeException(validation.getReason());
        }

        Appointment app = validation.getInfo().getMyApp();

        AppointmentDetailsDto dto = new AppointmentDetailsDto();
        dto.setId((long) app.getId());
        dto.setAppointmentDate(
                LocalDateTime.ofInstant(
                        app.getStartTime(),
                        ZoneId.systemDefault()
                )
        );
        dto.setTrainerName(app.getMyTrainer().getUsername());
        dto.setUserName(app.getMyTrainee().getUsername());

        // weather (only for future appointments)
        if (!app.getStartTime().isBefore(Instant.now())) {
        	WeatherReportDto weatherReport = weatherService.getAthensWeatherAtInstant(app.getStartTime())
    				.map(WeatherReportDto::createReport)
    				.defaultIfEmpty(WeatherReportDto.unavailable())
    				.block();
            dto.setWeather(weatherReport);
        }else {
        	dto.setWeather(WeatherReportDto.createNullReport());
        }
        

        return dto;
    }

    private AppointmentResponseDto toResponseDto(Appointment app) {
        AppointmentResponseDto dto = new AppointmentResponseDto();
        dto.setId((long) app.getId());
        dto.setAppointmentDate(
                LocalDateTime.ofInstant(
                        app.getStartTime(),
                        ZoneId.systemDefault()
                )
        );
        dto.setTrainerName(app.getMyTrainer().getUsername());
        dto.setTraineeName(app.getMyTrainee().getUsername());
        return dto;
    }
}

package com.example.FitTrack.service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.example.FitTrack.dto.WeatherReportDto;
import com.example.FitTrack.dto.validation.AvailabilityValidationInfo;
import com.example.FitTrack.dto.validation.AvailabilityValidationResult;
import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.entities.Availability;
import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.enums.AppointmentStatus;

@Service
public class ScheduleService {

	private SiteUserService userService;
	private AppointmentService appService;
	private AvailabilityService availService;
	private WeatherService weatherService;
	
	private final int MAX_APPOINTMENTS = 3;
	
	
	public ScheduleService(SiteUserService userService, AppointmentService appService, AvailabilityService availService,
			WeatherService weatherService) {
		this.userService = userService;
		this.appService = appService;
		this.availService = availService;
		this.weatherService = weatherService;
	}
	
	
	//in this method we will verify that the info given is valid, that the user can request an appointment etc
	public AvailabilityValidationResult verifyRequest(boolean isRecurring,Integer id, Integer inst, User user) {
		//getting the user
		Optional<SiteUser> givenTrainee = userService.getUserByUsername(user.getUsername());
		if (givenTrainee.isEmpty()) {
			System.out.println("user does not exist, this should never be called as we are already logged in as the user that supposedly doesnt exist");
			return new AvailabilityValidationResult(false, "Current user does not exist", null);
		}
		SiteUser confirmedTrainee = givenTrainee.get();
		
		//checking if their active appointments exceed the limit
		List<Appointment> traineeAppointments = confirmedTrainee.getAppointmentsAsTrainee();
		int activeAppointments = 0;
		for (Appointment app: traineeAppointments) {
			AppointmentStatus status = app.getStatus();
			if (status.equals(AppointmentStatus.Requested) || status.equals(AppointmentStatus.Accepted)) {
				activeAppointments++;
			}
		}
		//depending on whether they exceed max active appointments we return a valid or invalid result
		if (activeAppointments >= MAX_APPOINTMENTS) {
			return new AvailabilityValidationResult(false, "You have exceeded the maximum amount of active appointments (3)", null);
		}
		
		//find the given availability (if it exists) and get info from it
		Optional<Availability> givenAvail = availService.getAvailById(id);
		if (givenAvail.isEmpty()) {
			return new AvailabilityValidationResult(false, "You are trying to schedule an appointment on a non existent slot", null);
		}
		Availability confirmedAvail = givenAvail.get();
		int trainerId = confirmedAvail.getMyTrainer().getId();
		SiteUser confirmedTrainer = userService.getUserById(trainerId).get();
		
		//time figuring out
		ZoneId athensZone = ZoneId.of("Europe/Athens");
		Instant startInstant;
		Instant endInstant;
		
		if (!isRecurring && inst == null) {
			//this part should only accept onetime availabilities so we have a check here
			if (!confirmedAvail.isOneTime()) {
				return new AvailabilityValidationResult(false, "You have entered a faulty url for your requested appointment", null);
			}
			
			startInstant = ZonedDateTime.of(confirmedAvail.getDate(), confirmedAvail.getStartTime(), athensZone).toInstant();
			endInstant = ZonedDateTime.of(confirmedAvail.getDate(), confirmedAvail.getEndTime(), athensZone).toInstant();
			
		}else {
			//this part should only accept recurring availabilities so we have a check here
			if (confirmedAvail.isOneTime()) {
				return new AvailabilityValidationResult(false, "You have entered a faulty url for your requested appointment", null);
			}
			
			LocalDate curDay = LocalDate.now();
			DayOfWeek targetDay = confirmedAvail.getDay();
			LocalDate firstInstanceOfAvailability = curDay.with(TemporalAdjusters.nextOrSame(targetDay));
			
			
			 startInstant = ZonedDateTime.of(firstInstanceOfAvailability, confirmedAvail.getStartTime(), athensZone).plusWeeks(inst).toInstant();
			 endInstant = ZonedDateTime.of(firstInstanceOfAvailability, confirmedAvail.getEndTime(), athensZone).plusWeeks(inst).toInstant();
			
		}
		
		//check if the start time is in the past
		if (startInstant.isBefore(Instant.now())) {
			return new AvailabilityValidationResult(false, "You cannot set an appointment in the past", null);
		}
		
		
		/*
		 * ελεγχουμε αν ο trainer εχει  καποιο appointment στην ωρα που θελουμε να κανουμε request
		 * κανονικα αν ειχε ηδη επιβεβαιωσει τοτε δεν θα βλεπαμε το availability στο ημερολογιο
		 * αρα δεν θα ερχομασταν σε αυτη τη σελιδα, αλλα και παλι μπορει να επιβεβαιωσει καθως την φορτωνουμε
		 * ή καποιος χρηστης να ερθει εδω γραφοντας τυχαιους αριθμους στο url αντι απο το ημερολογιο 
		 */
		List<Appointment> confictingAppointmentsTrainer = appService.getOverlappingAppointmentsByTrainerAndTime(confirmedTrainer.getId(), startInstant, endInstant);
		for (Appointment app: confictingAppointmentsTrainer) {
			if (app.getStatus().equals(AppointmentStatus.Accepted)) {
				return new AvailabilityValidationResult(false, "The trainer has already accepted an appointment at this time", null);
			}
		}
		
		//check if the applying trainee already has any conflicting appointments at this time
		List<Appointment> conflictingAppointmentsTrainee = appService.getOverlappingAppointmentsByTraineeAndTime(confirmedTrainee.getId(), startInstant, endInstant);
		for (Appointment app: conflictingAppointmentsTrainee) {
			//if the trainee has any appointments at that time that are not either canceled or rejected
			//then they are not allowed to create a new one
			AppointmentStatus st = app.getStatus();
			if ((!st.equals(AppointmentStatus.Canceled)) && (!st.equals(AppointmentStatus.Rejected))) {
				return new AvailabilityValidationResult(false, "You already have an appointment scheduled for that time", null);
			}
		}
		
		//check weather if possible
		WeatherReportDto weatherReport = weatherService.getAthensWeatherAtInstant(startInstant)
				.map(WeatherReportDto::createReport)
				.defaultIfEmpty(WeatherReportDto.unavailable())
				.block();
		
		
		//all checks passed successfuly, we now return the info
		AvailabilityValidationInfo info = new AvailabilityValidationInfo(
				confirmedTrainer,
				confirmedTrainee,
				confirmedAvail,
				startInstant,
				endInstant,
				weatherReport.toString()
				);
		return new AvailabilityValidationResult(true, null, info);
	}
	
}

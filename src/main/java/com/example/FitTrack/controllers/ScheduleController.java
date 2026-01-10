package com.example.FitTrack.controllers;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.FitTrack.dto.WeatherReportDto;
import com.example.FitTrack.dto.API_dto.ForecastBlock;
import com.example.FitTrack.dto.validation.AvailabilityValidationInfo;
import com.example.FitTrack.dto.validation.AvailabilityValidationResult;
import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.entities.Availability;
import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.enums.AppointmentStatus;
import com.example.FitTrack.service.AppointmentService;
import com.example.FitTrack.service.AvailabilityService;
import com.example.FitTrack.service.SiteUserService;
import com.example.FitTrack.service.UserRoleService;
import com.example.FitTrack.service.WeatherService;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {
	private SiteUserService userService;
	private AppointmentService appService;
	private AvailabilityService availService;
	private WeatherService weatherService;
	
	private final int MAX_APPOINTMENTS = 3;
	
	public ScheduleController(SiteUserService userService, AppointmentService appService,
			AvailabilityService availService, WeatherService weatherService) {
		this.userService = userService;
		this.appService = appService;
		this.availService = availService;
		this.weatherService = weatherService;
	}
	
	
	@PreAuthorize("hasRole('ROLE_TRAINEE')")
	@GetMapping("/{id}")
	public String createAppointment(@PathVariable Integer id, @AuthenticationPrincipal User user, Model model) {
		//verify we can schedule an appointment with this info
		AvailabilityValidationResult result = verifyRequest(false,id,null,user);
		
		//if not say why
		if (!result.isSuccess()) {
			model.addAttribute("availableCheck", false);
			model.addAttribute("failReason",result.getReason());
			return "/schedule/requestAppointment";
		}
		
		//if yes get the info and show it to the user
		AvailabilityValidationInfo info = result.getInfo();
		SiteUser confirmedTrainer = info.getConfirmedTrainer();
		SiteUser confirmedTrainee = info.getConfirmedTrainee();
		Availability confirmedAvail = info.getConfirmedAvailability();
		Instant startInstant = info.getStartInstant();
		Instant endInstant = info.getEndInstant();
		String weatherInfo = info.getWeatherInfo();
		
		
		
		
		
		model.addAttribute("availableCheck", true);
		model.addAttribute("trainerName", confirmedTrainer.getFirstName() + " " + confirmedTrainer.getLastName());
		model.addAttribute("date", confirmedAvail.getDate().toString());
		model.addAttribute("startTime", confirmedAvail.getStartTime().toString());
		model.addAttribute("endTime", confirmedAvail.getEndTime().toString());
		model.addAttribute("weather", weatherInfo);
		
		
		return "/schedule/requestAppointment";
	}
	
	
	
	
	
	@PreAuthorize("hasRole('ROLE_TRAINEE')")
	@PostMapping("/{id}")
	public String createAppointmentPost(@PathVariable Integer id, @AuthenticationPrincipal User user, Model model, RedirectAttributes redirectAttributes) {
		AvailabilityValidationResult result = verifyRequest(false,id,null,user);
		
		if (!result.isSuccess()) {
			redirectAttributes.addAttribute("availableCheck", false);
			redirectAttributes.addAttribute("failReason",result.getReason());
			return "redirect:/schedule/requestAppointment";
		}
		
		AvailabilityValidationInfo info = result.getInfo();
		SiteUser confirmedTrainer = info.getConfirmedTrainer();
		SiteUser confirmedTrainee = info.getConfirmedTrainee();
		Availability confirmedAvail = info.getConfirmedAvailability();
		Instant startInstant = info.getStartInstant();
		Instant endInstant = info.getEndInstant();
		String weatherInfo = info.getWeatherInfo();
		
		
		//add info to redirect, whether we succeed or fail we will show it again
		redirectAttributes.addAttribute("availableCheck", true);		
		redirectAttributes.addAttribute("trainerName", confirmedTrainer.getFirstName() + " " + confirmedTrainer.getLastName());
		redirectAttributes.addAttribute("date", confirmedAvail.getDate().toString());
		redirectAttributes.addAttribute("startTime", confirmedAvail.getStartTime().toString());
		redirectAttributes.addAttribute("endTime", confirmedAvail.getEndTime().toString());
		model.addAttribute("weather", weatherInfo);
		
		//create new appointment with the given info and try to save it in the db
		Appointment newAppointment = new Appointment();
		newAppointment.setMyTrainer(confirmedTrainer);
		newAppointment.setMyTrainee(confirmedTrainee);
		newAppointment.setStartTime(startInstant);
		newAppointment.setEndTime(endInstant);
		newAppointment.setStatus(AppointmentStatus.Requested);
		
		try {
			Appointment savedAppointment = appService.saveAppointment(newAppointment);
			return "redirect:/schedule/appointmentRequestedSuccessfully";
		} catch (Exception e) {
			redirectAttributes.addAttribute("availableCheck",false);
			redirectAttributes.addAttribute("failReason","appointment creation failed");
			return "redirect:/schedule/requestAppointment/"+id;
		}
	}
	
	
	
	
	
	
	
	@PreAuthorize("hasRole('ROLE_TRAINEE')")
	@GetMapping("/{id}/{inst}")
	public String createAppointmentFromRecurringAvailability(@PathVariable Integer id, @PathVariable Integer inst, @AuthenticationPrincipal User user, Model model) {
		AvailabilityValidationResult result = verifyRequest(true,id,inst,user);
		
		if (!result.isSuccess()) {
			model.addAttribute("availableCheck", false);
			model.addAttribute("failReason",result.getReason());
			return "/schedule/requestAppointment";
		}
		
		AvailabilityValidationInfo info = result.getInfo();
		SiteUser confirmedTrainer = info.getConfirmedTrainer();
		SiteUser confirmedTrainee = info.getConfirmedTrainee();
		Availability confirmedAvail = info.getConfirmedAvailability();
		Instant startInstant = info.getStartInstant();
		Instant endInstant = info.getEndInstant();
		String weatherInfo = info.getWeatherInfo();
		
		
		ZoneId athensZone = ZoneId.of("Europe/Athens");
		
		
		model.addAttribute("availableCheck", true);
		
		model.addAttribute("trainerName", confirmedTrainer.getFirstName() + " " + confirmedTrainer.getLastName());
		//changed this attribute to display the correct date
		model.addAttribute("date", startInstant.atZone(athensZone).toLocalDate().toString());
		model.addAttribute("startTime", confirmedAvail.getStartTime().toString());
		model.addAttribute("endTime", confirmedAvail.getEndTime().toString());
		model.addAttribute("weather", weatherInfo);
		
		
		return "/schedule/requestAppointment";
	}
	
	
	
	
	
	
	
	
	
	@PreAuthorize("hasRole('ROLE_TRAINEE')")
	@PostMapping("/{id}/{inst}")
	public String createAppointmentFromRecurringAvailabilityPost(@PathVariable Integer id, @PathVariable Integer inst, @AuthenticationPrincipal User user, Model model, RedirectAttributes redirectAttributes) {
		AvailabilityValidationResult result = verifyRequest(true,id,inst,user);
		
		if (!result.isSuccess()) {
			redirectAttributes.addAttribute("availableCheck", false);
			redirectAttributes.addAttribute("failReason",result.getReason());
			return "redirect:/schedule/requestAppointment";
		}
		
		AvailabilityValidationInfo info = result.getInfo();
		SiteUser confirmedTrainer = info.getConfirmedTrainer();
		SiteUser confirmedTrainee = info.getConfirmedTrainee();
		Availability confirmedAvail = info.getConfirmedAvailability();
		Instant startInstant = info.getStartInstant();
		Instant endInstant = info.getEndInstant();
		String weatherInfo = info.getWeatherInfo();
		
		
		
		ZoneId athensZone = ZoneId.of("Europe/Athens");
		
		
		redirectAttributes.addAttribute("availableCheck", true);
		
		redirectAttributes.addAttribute("trainerName", confirmedTrainer.getFirstName() + " " + confirmedTrainer.getLastName());
		redirectAttributes.addAttribute("date", startInstant.atZone(athensZone).toLocalDate().toString());
		redirectAttributes.addAttribute("startTime", confirmedAvail.getStartTime().toString());
		redirectAttributes.addAttribute("endTime", confirmedAvail.getEndTime().toString());
		redirectAttributes.addAttribute("weather", weatherInfo);
		
		
		
		
		
		Appointment newAppointment = new Appointment();
		newAppointment.setMyTrainer(confirmedTrainer);
		newAppointment.setMyTrainee(confirmedTrainee);
		newAppointment.setStartTime(startInstant);
		newAppointment.setEndTime(endInstant);
		newAppointment.setStatus(AppointmentStatus.Requested);
		
		try {
			Appointment savedAppointment = appService.saveAppointment(newAppointment);
			return "redirect:/schedule/appointmentRequestedSuccessfully";
		} catch (Exception e) {
			redirectAttributes.addAttribute("availableCheck",false);
			redirectAttributes.addAttribute("failReason","appointment creation failed");
			return "redirect:/schedule/requestAppointment/"+id+"/"+inst;
		}
		
	}
	
	
	
	
	
	//if we don't have this method here then when we return "redirect:/schedule/appointmentRequestedSuccessfully"
	//it thinks we are trying to access a /schedule/{id} page and tries to turn the "appointmentRequestedSuccessfully" string
	//into an integer
	@GetMapping("/appointmentRequestedSuccessfully")
	public String appointmentRequestedSuccessfully() {
	    return "/schedule/appointmentRequestedSuccessfully";
	}

	
	
	//helper method
	
	//in this method we will verify that the info given by the url is valid, that the user can request an appointment etc
		private AvailabilityValidationResult verifyRequest(boolean isRecurring,Integer id, Integer inst, User user) {
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


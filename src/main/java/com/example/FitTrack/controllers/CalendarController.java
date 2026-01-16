package com.example.FitTrack.controllers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.FitTrack.dto.EventDto;
import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.entities.Availability;
import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.entities.UserRole;
import com.example.FitTrack.enums.AppointmentStatus;
import com.example.FitTrack.service.SiteUserService;
import com.example.FitTrack.service.UserRoleService;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

	private SiteUserService userService;
	private UserRoleService roleService;
	
	
	public CalendarController(SiteUserService userService, UserRoleService roleService) {
		this.userService = userService;
		this.roleService = roleService;
	}
	
	//general get method, will behave differently for trainers and trainees
	@PreAuthorize("isAuthenticated()")
	@GetMapping("")
	public String displayMyCalendar(@AuthenticationPrincipal User user, Authentication authentication, Model model) {
		
		boolean isTrainer = authentication.getAuthorities().stream()
	            .anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER"));
		
		if (isTrainer) {
			return displayMyCalendarTrainer(user, model);
		}else {
			return displayMyCalendarTrainee(user, model);
		}
	}
	
	//trainer method
	private String displayMyCalendarTrainer(User user, Model model) {
		Optional<SiteUser> givenTrainer = userService.getUserByUsername(user.getUsername());
		if (givenTrainer.isEmpty()) {
			model.addAttribute("errorReason","user does not exist, this should never be called as we are already logged in as the user that supposedly doesnt exist");
			return "/error";
		}
		SiteUser confirmedTrainer = givenTrainer.get();
		
		List<Appointment> appointments = confirmedTrainer.getAppointmentsAsTrainer();
		List<Availability> availabilities = confirmedTrainer.getMyAvailabilities();
		
		
		List<EventDto> events = new ArrayList<>();
		events.addAll(convertAppointmentsToEvents(appointments));
		events.addAll(convertAvailabilitiesToEvents(availabilities));
		
		
		
		model.addAttribute("eventList", events);
		
		return "calendar/privateCalendar";
	}
	
	
	//trainee method
	private String displayMyCalendarTrainee(User user, Model model) {
		Optional<SiteUser> givenTrainee = userService.getUserByUsername(user.getUsername());
		if (givenTrainee.isEmpty()) {
			model.addAttribute("errorReason","user does not exist, this should never be called as we are already logged in as the user that supposedly doesnt exist");
			return "/error";
		}
		SiteUser confirmedTrainee = givenTrainee.get();
		
		List<Appointment> appointments = confirmedTrainee.getAppointmentsAsTrainee();
		
		List<EventDto> events = new ArrayList<>();
		events.addAll(convertAppointmentsToEvents(appointments));
		
		model.addAttribute("eventList", events);
		
		return "calendar/privateCalendar";
	}
	
	
	
	
	//method for seeing a trainer's public calendar
	@GetMapping("/trainer/view/{id}")
	public String displayPublicCalendar(@PathVariable Integer id, Model model) {
		UserRole trainerRole = roleService.getRoleByName("ROLE_TRAINER");
		
		Optional<SiteUser> requestedUser = userService.getUserById(id);
		if (requestedUser.isEmpty()) {
			model.addAttribute("errorReason","the requested user does not exist");
			return "/error";
		}
		SiteUser requestedTrainer = requestedUser.get();
		
		if (requestedTrainer.getRoles().contains(trainerRole)) {
			//the requested user is a trainer
			List<Availability> availabilities = requestedTrainer.getMyAvailabilities();
			List<EventDto> events = new ArrayList<>();
			events.addAll(convertAvailabilitiesToEvents(availabilities));
			model.addAttribute("eventList", events);
			model.addAttribute("trainerName",requestedTrainer.getUsername());
			model.addAttribute("trainerId",requestedTrainer.getId());
			
			return "/trainers/trainerPublicCalendar";
		}else {
			//the requested user is not a trainer
			model.addAttribute("errorReason","the requested user is not a trainer");
			return "/error";
		}
	}
	
	
	
	
	
	
	
	
	
	//helper methods for converting appointments or availabilities to EventDtos
	private List<EventDto> convertAppointmentsToEvents(List<Appointment> appointments){
		ZoneId athensZone = ZoneId.of("Europe/Athens");
		List<EventDto> events = new ArrayList<>();
		
		for (Appointment app: appointments) {
			//colors
			String color = "";
			AppointmentStatus status = app.getStatus();
			
			if (status.equals(AppointmentStatus.Requested)) {
				color = "#A3A5FF";
			} else if (status.equals(AppointmentStatus.Accepted)) {
				color = "#0600FF";
			} else if (status.equals(AppointmentStatus.Rejected)) {
				color = "#737373";
			} else if (status.equals(AppointmentStatus.Completed)) {
				color = "#040050";
			}else {
				color = "#FF0000";
			}
			
			//times
			LocalDateTime startLocal = LocalDateTime.ofInstant(app.getStartTime(), athensZone);
		    LocalDateTime endLocal = LocalDateTime.ofInstant(app.getEndTime(), athensZone);
			
			//create event
			EventDto ev = new EventDto(
					app.getId(),
					startLocal.toString(),
					endLocal.toString(),
					color,
					"appointment",
					-1
					);
			
			events.add(ev);
		}
		
		return events;
	}
	
	private List<EventDto> convertAvailabilitiesToEvents(List<Availability> availabilities){
		List<EventDto> events = new ArrayList<>();
		for (Availability avail: availabilities) {
			if (avail.isOneTime()) {
				//onetime availabilities στο παρελθον δεν μας χρειαζονται και δεν τα δειχνουμε
				if (LocalDateTime.of(avail.getDate(), avail.getStartTime()).isAfter(LocalDateTime.now())) {
					EventDto ev = new EventDto(
							avail.getId(),
							LocalDateTime.of(avail.getDate(), avail.getStartTime()).toString(),
							LocalDateTime.of(avail.getDate(), avail.getEndTime()).toString(),
							"#4fbf36",
							"availability_onetime",
							-1
							);
					
					events.add(ev);
				}
			}else {
				events.addAll(createRecurringAvailability(avail));
			}
		}
		return events;
	}
	
	private List<EventDto> createRecurringAvailability(Availability avail){
		List<EventDto> events = new ArrayList<>();
		
		
		LocalDate curDay = LocalDate.now();
		DayOfWeek targetDay = avail.getDay();
		
		//αν του ζητησουμε πχ πεμπτη βρισκει την πρωτη πεμπτη απο το σημερα (συμπεριλαμβανοντας το σημερα)
		LocalDate firstDate = curDay.with(TemporalAdjusters.nextOrSame(targetDay));
		
		//52 εβδομαδες = 1 χρονος
		for (int i=0;i<52;i++) {
			LocalDate eventDate = firstDate.plusWeeks(i);
			
			EventDto event = new EventDto(
					avail.getId(), //ιδιο id για ολες τις επαναληψεις αυτου του availability
					LocalDateTime.of(eventDate, avail.getStartTime()).toString(),
					LocalDateTime.of(eventDate, avail.getEndTime()).toString(),
					"#4fbf36",
					"availability_recurring",
					i
					);
			
			events.add(event);
		}
		return events;
		
		
	}
}

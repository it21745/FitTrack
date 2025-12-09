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
@RequestMapping("/trainers")
public class TrainerController {

	private SiteUserService userService;
	private UserRoleService roleService;
	

	public TrainerController(SiteUserService userService, UserRoleService roleService) {
		this.userService = userService;
		this.roleService = roleService;
	}
	
	@GetMapping("")
	public String showAllTrainers(Model model) {
		model.addAttribute("trainer_list",userService.getUsersByRole(roleService.getRoleByName("ROLE_TRAINER")).get());
		return "trainers/trainerList";
	}
	
	private List<EventDto> createRecuringAvailability(Availability avail){
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
					"#ff0000",
					"availability_recurring",
					i
					);
			
			events.add(event);
		}
		return events;
		
		
	}
	
	private List<EventDto> convertAppointmentsToEvents(List<Appointment> appointments){
		ZoneId athensZone = ZoneId.of("Europe/Athens");
		List<EventDto> events = new ArrayList<>();
		
		for (Appointment app: appointments) {
			String color = "#0000ff";
			AppointmentStatus status = app.getStatus();
			
			if (status.equals(AppointmentStatus.Canceled)) {
				color = "#0000ff";
			}
			//βαζουμε χρωματα για τις αλλες περιπτωσεις αργοτερα
			
			
			LocalDateTime startLocal = LocalDateTime.ofInstant(app.getStartTime(), athensZone);
		    LocalDateTime endLocal = LocalDateTime.ofInstant(app.getEndTime(), athensZone);
			
			
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
				//onetime availabilities πριν το τωρα δεν μας χρειαζονται
				if (LocalDateTime.of(avail.getDate(), avail.getStartTime()).isAfter(LocalDateTime.now())) {
					EventDto ev = new EventDto(
							avail.getId(),
							LocalDateTime.of(avail.getDate(), avail.getStartTime()).toString(),
							LocalDateTime.of(avail.getDate(), avail.getEndTime()).toString(),
							"#ff0000",
							"availability_onetime",
							-1
							);
					
					events.add(ev);
				}
			}else {
				events.addAll(createRecuringAvailability(avail));
			}
		}
		return events;
	}
	
	@PreAuthorize("hasRole('ROLE_TRAINER')")
	@GetMapping("/calendar")
	public String showCalendar(@AuthenticationPrincipal User user, Model model) {
			
		Optional<SiteUser> curUser = userService.getUserByUsername(user.getUsername());
		if (curUser.isEmpty()) {
			System.out.println("user does not exist, this should never be called as we are already logged in as the user that supposedly doesnt exist");
			return "/error";
		}
		SiteUser curTrainer = curUser.get();
		
		
		List<Appointment> appointments = curTrainer.getAppointmentsAsTrainer();
		List<Availability> availabilities = curTrainer.getMyAvailabilities();
		
		
		List<EventDto> events = new ArrayList<>();
		events.addAll(convertAppointmentsToEvents(appointments));
		events.addAll(convertAvailabilitiesToEvents(availabilities));
		
		
		
		model.addAttribute("eventList", events);
		
		return "trainers/trainerCalendar";
		
		
		
	}
	
	
	@GetMapping("/availabilities/view/{id}")
	public String seeAvailabilities(@PathVariable Integer id, Model model) {
		UserRole trainerRole = roleService.getRoleByName("ROLE_TRAINER");
		
		Optional<SiteUser> requestedUser = userService.getUserById(id);
		if (requestedUser.isEmpty()) {
			System.out.println("the requested user does not exist");
			return "/error";
		}
		SiteUser requestedTrainer = requestedUser.get();
		
		if (requestedTrainer.getRoles().contains(trainerRole)) {
			//the requested user is a trainer
			List<Availability> availabilities = requestedTrainer.getMyAvailabilities();
			List<EventDto> events = new ArrayList<>();
			events.addAll(convertAvailabilitiesToEvents(availabilities));
			model.addAttribute("eventList", events);
			
			return "/trainers/trainerPublicCalendar";
		}else {
			//the requested user is not a trainer
			System.out.println("the requested user is not a trainer");
			return "/error";
		}
	}
	
	
}

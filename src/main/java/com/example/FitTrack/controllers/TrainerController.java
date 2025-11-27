package com.example.FitTrack.controllers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.FitTrack.dto.EventDto;
import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.entities.Availability;
import com.example.FitTrack.entities.SiteUser;
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
		model.addAttribute("trainer_list",userService.getUsersByRole(roleService.getRoleByName("ROLE_TRAINER")));
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
					"availability_recurring"
					);
			
			events.add(event);
		}
		return events;
		
		
	}
	
	@GetMapping("/calendar")
	public String showCalendar(@AuthenticationPrincipal User user, Model model) {
		if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER"))) {
			
			SiteUser curTrainer = userService.getUserByUsername(user.getUsername());
			List<Appointment> appointments = curTrainer.getAppointmentsAsTrainer();
			List<Availability> availabilities = curTrainer.getMyAvailabilities();
			
			ZoneId athensZone = ZoneId.of("Europe/Athens");

			
			//μετατρεπουμε τα appointments και τα availabilities σε events για το ημερολογιο
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
						"appointment"
						);
				
				events.add(ev);
			}
			
			for (Availability avail: availabilities) {
				if (avail.isOneTime()) {
					EventDto ev = new EventDto(
							avail.getId(),
							LocalDateTime.of(avail.getDate(), avail.getStartTime()).toString(),
							LocalDateTime.of(avail.getDate(), avail.getEndTime()).toString(),
							"#ff0000",
							"availability_onetime"
							);
					
					events.add(ev);
				}else {
					events.addAll(createRecuringAvailability(avail));
				}
			}
			
			model.addAttribute("eventList", events);
			
			return "trainers/trainerCalendar";
		}else {
			return "/error";
		}
		
		
		
	}
	
	
}

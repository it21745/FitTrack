package com.example.FitTrack.schedule;

import java.time.Instant;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.enums.AppointmentStatus;
import com.example.FitTrack.repository.AppointmentRepository;

@Service
public class AppointmentUpdater {
    private AppointmentRepository repo;

    public AppointmentUpdater(AppointmentRepository repo) {
        this.repo = repo;
    }
    
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void updateAllAppointments() {
    	//this method is run once every minute
    	//it finds all appointments that have ended (and are either requested or accepted)
    	//and updates them to canceled or completed
    	Instant now = Instant.now();
    	List<Appointment> expiredApps = repo.findExpiredAppointments(
    			now,
    			List.of(AppointmentStatus.Accepted, AppointmentStatus.Requested));
    	
    	for (Appointment app: expiredApps) {
    		if (app.getStatus().equals(AppointmentStatus.Accepted)) {
    			app.setStatus(AppointmentStatus.Completed);
    		}else if (app.getStatus().equals(AppointmentStatus.Requested)) {
    			app.setStatus(AppointmentStatus.Rejected);
    		}
    	}
    	//apparently we don't need to actually save the appointments to the db, it's done automatically
    }
    
    
}

package com.example.FitTrack.repository;

import com.example.FitTrack.entities.Appointment;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
	List<Appointment> findByStatus(String status);
	
	
	//jpsql χρησιμοποιει ονοματα οντοτητων java, οχι τα ονοματα που εχουν στην βαση
	@Query("SELECT a FROM Appointment a "+
			"WHERE a.myTrainer.id = :trainerId "+
			"AND a.startTime <= :availEnd " +
			"AND a.endTime >= :availStart"
			)
	List<Appointment> findOverlappingAppointmentsTrainer(
			@Param("trainerId") Integer trainerId,
			@Param("availStart") Instant availStart,
			@Param("availEnd") Instant availEnd
			);
	
	@Query("SELECT a FROM Appointment a "+
			"WHERE a.myTrainee.id = :traineeId "+
			"AND a.startTime <= :availEnd " +
			"AND a.endTime >= :availStart"
			)
	List<Appointment> findOverlappingAppointmentsTrainee(
			@Param("traineeId") Integer traineeId,
			@Param("availStart") Instant availStart,
			@Param("availEnd") Instant availEnd
			);
	
}

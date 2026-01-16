package com.example.FitTrack.repository;

import com.example.FitTrack.entities.Availability;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {

	Optional<Availability> findById(int id);
	List<Availability> findByMyTrainerId(int trainerId);

}

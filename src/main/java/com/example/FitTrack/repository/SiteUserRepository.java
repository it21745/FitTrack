package com.example.FitTrack.repository;

import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.entities.UserRole;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteUserRepository extends JpaRepository<SiteUser, Integer> {
	
	//apparently this works automatically due to spring magic
	//it sees the method "findByRoles" and understands what it should do and implements it on its own
	List<SiteUser> findByRoles(UserRole role);
//	SiteUser findByUsername(String username);
	Optional<SiteUser> findByUsername(String username);
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
}

package com.example.FitTrack.repository;

import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.enums.UserRole;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteUserRepository extends JpaRepository<SiteUser, Integer> {
	
	//apparently this works automatically due to spring magic
	//it sees the method "findByRole" and understands what it should do and implements it on its own
	List<SiteUser> findByRole(UserRole role);
	SiteUser findByUsername(String username);
}

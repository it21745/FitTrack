package com.example.FitTrack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.FitTrack.entities.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
	Optional<UserRole> findByName(String name);
	
	
	default UserRole updateOrInsert(UserRole role) {
		UserRole preExistingRole = findByName(role.getName()).orElse(null);
		
		if (preExistingRole != null) {
			return preExistingRole;
		}else {
			return save(role);
		}
	}
}

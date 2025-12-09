package com.example.FitTrack.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.FitTrack.repository.AppointmentRepository;
import com.example.FitTrack.repository.SiteUserRepository;
import com.example.FitTrack.repository.UserRoleRepository;

import jakarta.transaction.Transactional;

import com.example.FitTrack.entities.Appointment;
import com.example.FitTrack.entities.SiteUser;
import com.example.FitTrack.entities.UserRole;

@Service
public class SiteUserService implements UserDetailsService {

	private SiteUserRepository userRepo;
	private UserRoleRepository roleRepo;
	private AppointmentRepository appRepo;
	private BCryptPasswordEncoder passwordEncoder;

	
	public SiteUserService(SiteUserRepository userRepo, UserRoleRepository roleRepo, AppointmentRepository appRepo, BCryptPasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.appRepo = appRepo;
		this.passwordEncoder = passwordEncoder;
	}
	
	//methods
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<SiteUser> opt = userRepo.findByUsername(username);
		
		if (opt.isEmpty()) {
			throw new UsernameNotFoundException("User with email: " +username +" not found !");
		}else {
			SiteUser user = opt.get();
			return new org.springframework.security.core.userdetails.User(
					user.getUsername(),
					user.getPassword(),
					user.getRoles()
							.stream()
							.map(role-> new SimpleGrantedAuthority(role.getName()))
							.collect(Collectors.toSet())
					);
		}
	}
	
    @Transactional
    public Integer saveUser(SiteUser user, Integer roleId) {
    	String passwd= user.getPassword();
        String encodedPassword = passwordEncoder.encode(passwd);
        user.setPassword(encodedPassword);


        UserRole role = null;
        if (roleId != null) {
            role = roleRepo.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Error: Selected role not found (id=" + roleId + ")"));
        } else {
            role = roleRepo.findByName("ROLE_TRAINEE")
                    .orElseThrow(() -> new RuntimeException("Error: Default role ROLE_TRAINEE not found."));
        }
        
        Set<UserRole> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        user = userRepo.save(user);
        return user.getId();
    }

    @Transactional
    public Integer updateUser(SiteUser user) {
        user = userRepo.save(user);
        return user.getId();
    }

	
	@Transactional
	public List<SiteUser> getAllUsers(){
		return userRepo.findAll();
	}
	
	@Transactional
	public Optional<List<SiteUser>> getUsersByRole(UserRole role){
		return userRepo.findByRoles(role);
	}
	
	@Transactional
	public Optional<SiteUser> getUserById(Integer id) {
		return userRepo.findById(id);
	}
	
	@Transactional
	public Optional<SiteUser> getUserByUsername(String username) {
		return userRepo.findByUsername(username);
	}



	
	
	
	
	
}

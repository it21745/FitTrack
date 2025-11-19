package com.example.FitTrack.entities;

import java.util.List;

import com.example.FitTrack.enums.UserRole;

import jakarta.persistence.*;

@Entity
public class SiteUser {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int id;
	
	@Column(nullable = false, unique = true, length = 50)
	private String username;
	
	@Column(nullable = false, unique = true, length = 50)
	private String email;
	
	@Column(nullable = false)
	private String passwordHash;
	
	@Column(nullable = false)
	private String firstName;
	
	@Column(nullable = false)
	private String lastName;
	
	@Lob
	@Column
	private String info;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;
	
	//orphanRemoval = true, δηλαδη αν διαγραψουμε τον trainer θα διαγραφουν τα availabilities του
	@OneToMany(mappedBy = "myTrainer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Availability> myAvailabilities;
	
    @OneToMany(mappedBy = "myTrainer", fetch = FetchType.LAZY)
    private List<Appointment> appointmentsAsTrainer;

    @OneToMany(mappedBy = "myTrainee", fetch = FetchType.LAZY)
    private List<Appointment> appointmentsAsTrainee;

	
    
    //constructors
    public SiteUser(String username, String email, String passwordHash, String firstName, String lastName, String info,
			UserRole role, List<Availability> myAvailabilities, List<Appointment> appointmentsAsTrainer,
			List<Appointment> appointmentsAsTrainee) {
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		this.firstName = firstName;
		this.lastName = lastName;
		this.info = info;
		this.role = role;
		this.myAvailabilities = myAvailabilities;
		this.appointmentsAsTrainer = appointmentsAsTrainer;
		this.appointmentsAsTrainee = appointmentsAsTrainee;
	}
    
    public SiteUser() {}

    //getters and setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public List<Availability> getMyAvailabilities() {
		return myAvailabilities;
	}

	public void setMyAvailabilities(List<Availability> myAvailabilities) {
		this.myAvailabilities = myAvailabilities;
	}

	public List<Appointment> getAppointmentsAsTrainer() {
		return appointmentsAsTrainer;
	}

	public void setAppointmentsAsTrainer(List<Appointment> appointmentsAsTrainer) {
		this.appointmentsAsTrainer = appointmentsAsTrainer;
	}

	public List<Appointment> getAppointmentsAsTrainee() {
		return appointmentsAsTrainee;
	}

	public void setAppointmentsAsTrainee(List<Appointment> appointmentsAsTrainee) {
		this.appointmentsAsTrainee = appointmentsAsTrainee;
	}
    
    
	
}

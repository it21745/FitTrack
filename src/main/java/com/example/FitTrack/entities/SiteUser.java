package com.example.FitTrack.entities;

import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(	name = "site_users")
public class SiteUser {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int id;
	
	@Column(nullable = false, unique = true, length = 50)
	@NotBlank(message = "Username cannot be blank")
	@Size(min = 3, max = 50, message = "Username cannot be under 3 or over 50 characters")
	private String username;
	
	@Column(nullable = false, unique = true, length = 50)
	@NotBlank(message = "Email cannot be blank")
	@Email
	private String email;
	
	@Column(nullable = false, length = 255)
	@NotBlank(message = "Password cannot be blank")
	private String password;
	
	@Column(nullable = false)
	@NotBlank(message = "Name cannot be blank")
	private String firstName;
	
	@Column(nullable = false)
	@NotBlank(message = "Surname cannot be blank")
	private String lastName;
	
	@Lob
	@Column
	private String info;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_roles",
    	joinColumns = @JoinColumn(name = "user_id"),
    	inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<UserRole> roles;
	
	//orphanRemoval = true, δηλαδη αν διαγραψουμε τον trainer θα διαγραφουν τα availabilities του
	@OneToMany(mappedBy = "myTrainer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Availability> myAvailabilities;
	
    @OneToMany(mappedBy = "myTrainer", fetch = FetchType.LAZY)
    private List<Appointment> appointmentsAsTrainer;

    @OneToMany(mappedBy = "myTrainee", fetch = FetchType.LAZY)
    private List<Appointment> appointmentsAsTrainee;

	
    
    //constructors
    
    
    public SiteUser() {}

    public SiteUser(
			@NotBlank(message = "Username cannot be blank") @Size(min = 3, max = 50, message = "Username cannot be under 3 or over 50 characters") String username,
			@NotBlank(message = "Email cannot be blank") @Email String email,
			@NotBlank(message = "Password cannot be blank") @Size(min = 3, max = 50, message = "Password cannot be under 3 or over 50 characters") String password,
			@NotBlank(message = "Name cannot be blank") String firstName,
			@NotBlank(message = "Surname cannot be blank") String lastName) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
	}

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Set<UserRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<UserRole> roles) {
		this.roles = roles;
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

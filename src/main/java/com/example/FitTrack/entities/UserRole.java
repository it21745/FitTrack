package com.example.FitTrack.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class UserRole {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int id;
	
	@Column(length = 20)
    private String name;

	
	//constructors
	public UserRole(String name) {
		super();
		this.name = name;
	}
	
	public UserRole() {}

	
	
	//getters and setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}

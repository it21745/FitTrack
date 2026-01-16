package com.example.FitTrack.dto.API_dto.trainers;

import com.example.FitTrack.entities.SiteUser;

public class TrainerDto {
	private int id;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String info;
	private String fitnessProfileJson;
	
	private TrainerDto() {}
	
	public static TrainerDto TrainerToDto(SiteUser user) {
		TrainerDto trainer = new TrainerDto();
		trainer.setId(user.getId());
		trainer.setUsername(user.getUsername());
		trainer.setEmail(user.getEmail());
		trainer.setFirstName(user.getFirstName());
		trainer.setLastName(user.getLastName());
		trainer.setInfo(user.getInfo());
		trainer.setFitnessProfileJson(user.getFitnessProfileJson());
		
		return trainer;
	}
	
	
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
	public String getFitnessProfileJson() {
		return fitnessProfileJson;
	}
	public void setFitnessProfileJson(String fitnessProfileJson) {
		this.fitnessProfileJson = fitnessProfileJson;
	}
	
	
}

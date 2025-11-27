package com.example.FitTrack.entities.helper_entities;


public class UserInfo {
	private double weight;
	private double height;
	private UserGoal goal;
	
	
	
	
	public UserInfo() {}
	
	public UserInfo(double weight, double height, UserGoal goal) {
		this.weight = weight;
		this.height = height;
		this.goal = goal;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public UserGoal getGoal() {
		return goal;
	}

	public void setGoal(UserGoal goal) {
		this.goal = goal;
	}
	
	
	
}

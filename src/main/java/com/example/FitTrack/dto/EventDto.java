package com.example.FitTrack.dto;

public class EventDto {
	private int id;
	private String start;
	private String end;
	private String color;
	private String type;
	
	
	public EventDto(int id, String start, String end, String color, String type) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.color = color;
		this.type = type;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getStart() {
		return start;
	}


	public void setStart(String start) {
		this.start = start;
	}


	public String getEnd() {
		return end;
	}


	public void setEnd(String end) {
		this.end = end;
	}


	public String getColor() {
		return color;
	}


	public void setColor(String color) {
		this.color = color;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
	
}

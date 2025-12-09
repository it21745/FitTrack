package com.example.FitTrack.dto;

public class EventDto {
	private int id;
	private String start;
	private String end;
	private String color;
	private String type;
	private int instanceIndex;
	
	
	public EventDto(int id, String start, String end, String color, String type, int instanceIndex) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.color = color;
		this.type = type;
		this.instanceIndex = instanceIndex;
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


	public int getInstanceIndex() {
		return instanceIndex;
	}


	public void setInstanceIndex(int instanceIndex) {
		this.instanceIndex = instanceIndex;
	}
	
	
	
	
	
}

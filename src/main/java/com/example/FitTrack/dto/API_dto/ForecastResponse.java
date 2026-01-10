package com.example.FitTrack.dto.API_dto;

import java.util.List;

public class ForecastResponse {
	private List<ForecastBlock> list;
	

	public List<ForecastBlock> getList() {
		return list;
	}

	public void setList(List<ForecastBlock> list) {
		this.list = list;
	}
	
	
}

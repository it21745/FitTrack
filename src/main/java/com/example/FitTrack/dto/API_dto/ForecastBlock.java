package com.example.FitTrack.dto.API_dto;

import java.time.Instant;
import java.util.List;

public class ForecastBlock {
	private long dt;  //unix time, openweather calls it datetime
	private ForecastBlockMainInfo main;
	private List<ForecastBlockWeatherInfo> weather;
	private Double pop;
	

	public long getDt() {
		return dt;
	}

	public void setDt(long dt) {
		this.dt = dt;
	}
	
	public ForecastBlockMainInfo getMain() {
		return main;
	}

	public void setMain(ForecastBlockMainInfo main) {
		this.main = main;
	}

	public ForecastBlockWeatherInfo getWeather() {
		if (weather == null || weather.isEmpty()) {
			return null;
		}
		return weather.get(0);
	}

	public void setWeather(List<ForecastBlockWeatherInfo> weather) {
		this.weather = weather;
	}
	
	public Double getPop() {
		return pop;
	}

	public void setPop(Double pop) {
		this.pop = pop;
	}

	
	
	//getters of useful metrics
	
	public Instant getInstant() {
		return Instant.ofEpochSecond(dt);
	}
	
	


}

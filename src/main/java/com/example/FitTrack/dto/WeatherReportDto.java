package com.example.FitTrack.dto;

import com.example.FitTrack.dto.API_dto.ForecastBlock;

public class WeatherReportDto {
	private boolean available;
	private double temp;
	private double pop;
	private String cloudMain;
	private String cloudDescription;
	
	public static WeatherReportDto unavailable() {
		WeatherReportDto dto = new WeatherReportDto();
		dto.setAvailable(false);
		return dto;
	}
	
	public static WeatherReportDto createReport(ForecastBlock block) {
		WeatherReportDto dto = new WeatherReportDto();
		dto.setAvailable(true);
		dto.setTemp(block.getMain().getTemp());
		dto.setPop(block.getPop());
		dto.setCloudMain(block.getWeather().getMain());
		dto.setCloudDescription(block.getWeather().getDescription());
		
		return dto;
	}
	
	public static WeatherReportDto createNullReport() {
		WeatherReportDto dto = new WeatherReportDto();
		dto.setAvailable(false);
		dto.setTemp(0);
		dto.setPop(0);
		dto.setCloudMain("");
		dto.setCloudDescription("");
		
		return dto;
	}
	
	
	public String toString() {
		if (!isAvailable()) {
			return null;
		}
		return "Temperature: " + Math.round(temp)+"° | "
		+ "Chance of Rain: " + Math.round(pop * 100)+"% | "
		+ "Atmosphere: "+cloudDescription;
	}
	
	
	

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public double getPop() {
		return pop;
	}

	public void setPop(double pop) {
		this.pop = pop;
	}

	public String getCloudMain() {
		return cloudMain;
	}

	public void setCloudMain(String cloudMain) {
		this.cloudMain = cloudMain;
	}

	public String getCloudDescription() {
		return cloudDescription;
	}

	public void setCloudDescription(String cloudDescription) {
		this.cloudDescription = cloudDescription;
	}
	
	
	
}

package com.iot.app.spark.vo;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class StreamData implements Serializable{
	
	
	private String vehicleId;
	private String vehicleType;
	private String routeId;
	private String latitude;
	private String longitude;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="IST")
	private Date timestamp;
	private double speed;
	private double fuelLevel;
	
	public StreamData(){
		
	}
	
	public StreamData(String vehicleId, String vehicleType, String routeId, String latitude, String longitude, 
			Date timestamp, double speed, double fuelLevel) {
		super();
		this.vehicleId = vehicleId;
		this.vehicleType = vehicleType;
		this.routeId = routeId;
		this.longitude = longitude;
		this.latitude = latitude;
		this.timestamp = timestamp;
		this.speed = speed;
		this.fuelLevel = fuelLevel;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public String getRouteId() {
		return routeId;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public double getSpeed() {
		return speed;
	}

	public double getFuelLevel() {
		return fuelLevel;
}
}

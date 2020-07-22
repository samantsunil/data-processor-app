package com.iot.app.spark.vo;

import java.io.Serializable;

public class AggregateKey implements Serializable {
	
	private final String routeId;
	private final String vehicleType;
	
	public AggregateKey(String routeId, String vehicleType) {
		super();
		this.routeId = routeId;
		this.vehicleType = vehicleType;
	}

	public String getRouteId() {
		return routeId;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((routeId == null) ? 0 : routeId.hashCode());
		result = prime * result + ((vehicleType == null) ? 0 : vehicleType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj !=null && obj instanceof AggregateKey){
			AggregateKey other = (AggregateKey)obj;
			if(other.getRouteId() != null && other.getVehicleType() != null){
				if((other.getRouteId().equals(this.routeId)) && (other.getVehicleType().equals(this.vehicleType))){
					return true;
				} 
			}
		}
		return false;
	}
	

}

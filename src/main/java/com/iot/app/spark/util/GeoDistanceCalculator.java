package com.iot.app.spark.util;
public class GeoDistanceCalculator {
	public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
		//Earth radius in KM
		final int r = 6371;

		Double latDistance = Math.toRadians(lat2 - lat1);
		Double lonDistance = Math.toRadians(lon2 - lon1);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = r * c;
		
		return distance;
	}
	
	public static boolean isInPOIRadius(double currentLat, double currentLon, double poiLat, double poiLon,double radius){
		double distance = getDistance(currentLat,currentLon,poiLat,poiLon);
		return distance <= radius;
	}

}
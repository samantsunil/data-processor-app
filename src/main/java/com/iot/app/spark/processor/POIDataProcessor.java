package com.iot.app.spark.processor;

import static com.datastax.spark.connector.japi.CassandraStreamingJavaUtil.javaFunctions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.iot.app.spark.entity.POITrafficData;
import com.iot.app.spark.util.GeoDistanceCalculator;
import com.iot.app.spark.vo.StreamData;
import com.iot.app.spark.vo.POIData;

import scala.Tuple2;
import scala.Tuple3;

public class POIDataProcessor {

    private static final Logger logger = Logger.getLogger(POIDataProcessor.class);

    public void processPOIData(JavaDStream<StreamData> nonFilteredIotDataStream, Broadcast<Tuple3<POIData, String, String>> broadcastPOIValues) {

        // Filter by routeId,vehicleType and in POI range
        JavaDStream<StreamData> iotDataStreamFiltered = nonFilteredIotDataStream
                .filter(iot -> (iot.getRouteId().equals(broadcastPOIValues.value()._2())
                && iot.getVehicleType().contains(broadcastPOIValues.value()._3())
                && GeoDistanceCalculator.isInPOIRadius(Double.valueOf(iot.getLatitude()),
                        Double.valueOf(iot.getLongitude()), broadcastPOIValues.value()._1().getLatitude(),
                        broadcastPOIValues.value()._1().getLongitude(),
                        broadcastPOIValues.value()._1().getRadius())));

        // pair with poi
        JavaPairDStream<StreamData, POIData> poiDStreamPair = iotDataStreamFiltered
                .mapToPair(iot -> new Tuple2<>(iot, broadcastPOIValues.value()._1()));

        // Transform to dstream of POITrafficData
        JavaDStream<POITrafficData> trafficDStream = poiDStreamPair.map(poiTrafficDataFunc);

        // Map Cassandra table column
        Map<String, String> columnNameMappings = new HashMap<>();
        columnNameMappings.put("vehicleId", "vehicleid");
        columnNameMappings.put("distance", "distance");
        columnNameMappings.put("vehicleType", "vehicletype");
        columnNameMappings.put("timeStamp", "timestamp");
        trafficDStream.foreachRDD(rdd -> rdd.foreach(x -> System.out.println(x)));
        // call CassandraStreamingJavaUtil function to save in DB
        javaFunctions(trafficDStream)
                .writerBuilder("vehicledatakeyspace", "poi_traffic", CassandraJavaUtil.mapToRow(POITrafficData.class, columnNameMappings))
                .withConstantTTL(120)//keeping data alive only for 2 minutes
                .saveToCassandra();
    }

    //Function to create POITrafficData object from IoT data
    private static final Function<Tuple2<StreamData, POIData>, POITrafficData> poiTrafficDataFunc = (tuple -> {
        POITrafficData poiTraffic = new POITrafficData();
        poiTraffic.setVehicleId(tuple._1.getVehicleId());
        poiTraffic.setVehicleType(tuple._1.getVehicleType());
        poiTraffic.setTimeStamp(new Date());
        double distance = GeoDistanceCalculator.getDistance(Double.parseDouble(tuple._1.getLatitude()),
                Double.parseDouble(tuple._1.getLongitude()), tuple._2.getLatitude(), tuple._2.getLongitude());
        logger.debug("Distance for " + tuple._1.getLatitude() + "," + tuple._1.getLongitude() + "," + tuple._2.getLatitude() + "," + tuple._2.getLongitude() + " = " + distance);
        poiTraffic.setDistance(distance);
        return poiTraffic;
    });

}

package com.iot.app.spark.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.*;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.api.java.*;
import org.apache.spark.streaming.kafka010.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.iot.app.spark.util.StreamDataDecoder;
import com.iot.app.spark.util.PropertyFileReader;
import com.iot.app.spark.vo.StreamData;
import com.iot.app.spark.vo.POIData;
import org.apache.spark.streaming.Duration;
import org.apache.kafka.common.serialization.StringDeserializer;
import scala.Tuple2;
import scala.Tuple3;

public class StreamDataProcessor {

    private static final Logger logger = Logger.getLogger(StreamDataProcessor.class);

    public static void main(String[] args) throws Exception {
        //read Spark and Cassandra properties and create SparkConf
        Properties prop = PropertyFileReader.readPropertyFile();
        SparkConf conf = new SparkConf()
                .setAppName(prop.getProperty("com.iot.app.spark.app.name"))
                .set("spark.cassandra.connection.host", prop.getProperty("com.iot.app.cassandra.host"))
                .set("spark.cassandra.connection.port", prop.getProperty("com.iot.app.cassandra.port"))
                .set("spark.cassandra.connection.keep_alive_ms", prop.getProperty("com.iot.app.cassandra.keep_alive"));
        //batch interval of 1 seconds for incoming stream - fixed
        JavaSparkContext jsc = new JavaSparkContext(conf);
        JavaStreamingContext jssc = new JavaStreamingContext(jsc, new Duration(1000));
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("zookeeper.connect", prop.getProperty("com.iot.app.kafka.zookeeper"));
        kafkaParams.put("bootstrap.servers", prop.getProperty("com.iot.app.kafka.brokerlist"));
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StreamDataDecoder.class);
        kafkaParams.put("group.id", "iotProcessor");
        String topic = prop.getProperty("com.iot.app.kafka.topic");

        Collection<String> topics = Arrays.asList(topic);
        JavaInputDStream<ConsumerRecord<String, StreamData>> directStream = KafkaUtils.createDirectStream(jssc, LocationStrategies.PreferConsistent(), ConsumerStrategies.<String, StreamData>Subscribe(topics, kafkaParams));
        logger.info("Starts Stream Processing");

        JavaPairDStream<String, StreamData> results = directStream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));
        JavaDStream<StreamData> nonFilteredIotDataStream = results.map(tuple2 -> tuple2._2());

        POIDataProcessor poiDataProcessor = new POIDataProcessor();

        POIData poiData = new POIData();
        poiData.setLatitude(33.877495);
        poiData.setLongitude(-95.50238);
        poiData.setRadius(20);//20km range search       
        Broadcast<Tuple3<POIData, String, String>> broadcastPOIValues = jssc.sparkContext().broadcast(new Tuple3<>(poiData, "Route-M1", "Car"));

        poiDataProcessor.processPOIData(nonFilteredIotDataStream, broadcastPOIValues);
        //start context
        jssc.start();
        jssc.awaitTermination();
    }

}

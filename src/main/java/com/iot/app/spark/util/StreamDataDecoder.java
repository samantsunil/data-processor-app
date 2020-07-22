package com.iot.app.spark.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.app.spark.vo.StreamData;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

public class StreamDataDecoder implements Deserializer<StreamData> {
	
	//private static ObjectMapper objectMapper = new ObjectMapper();
		   // private boolean isKey;
 private final ObjectMapper objectMapper = new ObjectMapper();
  @Override
   public void configure(Map<String, ?> configs, boolean isKey) {
    }
    @Override
    public StreamData deserialize(String topic, byte[] objectData)
    {
        try {

                return objectMapper.readValue(objectData, StreamData.class);
            
        } catch (Exception ex) {
		}
        return null;
    }

    @Override
    public void close()
    {

    }
	
}

package com.iot.app.spark.util;

import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;
import org.apache.log4j.Logger;

public class PropertyFileReader {

    private static final Logger logger = Logger.getLogger(PropertyFileReader.class);
    private static final Properties prop = new Properties();

    public static Properties readPropertyFile() throws Exception {
        if (prop.isEmpty()) {

            FileInputStream input = null;
            String path = "/home/ubuntu/iot-spark.properties"; //config file in home directory
            try {
                input = new FileInputStream(path);
                prop.load(input);
            } catch (IOException ex) {
                logger.error(ex);
                throw ex;
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        }
        return prop;
    }
}

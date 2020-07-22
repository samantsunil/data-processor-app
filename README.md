# Spark Data Processor Application
Spark Data Processor is a maven application for processing Data streams such as IoT data using Apache Spark. Processed data is persisted into the Cassandra Database. This project requires following tools and technologies.
- JDK - 1.8
- Maven - 3.3.9
- Spark-Streaming-Kafka - 0.10-2.11
- Cassandra - 2.4.3
- Spark - 2.4.5 Pre-built for Hadoop 2.6

Please refer "CassandraDB.cql" file to create Keyspace and Table in Cassandra Database, which are required for this application to work. The script should be available in cassandra node when using CROODaP application. Also, the script for creating keyspace and table in cassandra cluster is available in CROODaP repository.
The kafka service should be running with the topic name provided created. The script for creating topic in the kafka service is also available in CROODaP repository.
The properties file io-spark.properties should be made available to home directory (ubuntu system).
For submitting the job follow spark-submit command or the script is provided in CROODaP application.
The application can be build using : sudo mvn package and the executable jar file need to be stored into the spark master node when using CROODaP for deployment and scaling.

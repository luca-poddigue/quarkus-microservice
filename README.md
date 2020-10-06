# Quarkus microservice

## Requirements
You only need OpenJDK 11 installed with JAVA_HOME configured appropriately. Maven will be installed automatically by the `mvnw` wrapper.

## Quick start
To start the service:
* `mvnw package`
* `java -jar target/challenge-1.0-SNAPSHOT-runner.jar`

To run in debug mode use `mvnw compile quarkus:dev`. The server will also listen for a debugger on port 5005.

## Docker
To start the service within a Docker container:
* `mvnw package`
* `docker build -f src/main/docker/Dockerfile.jvm -t cuebiq/challenge-jvm .`
* `docker run -it --rm -p 8080:8080 cuebiq/challenge-jvm`

## Endpoints
The service fetches the impressions dataset from a resource file and counts them grouping based on the chosen criterion.

#### Number of impressions by device
GET http://localhost:8080/api/impressionsCount/byDevice

#### Number of impressions by time span
GET http://localhost:8080/api/impressionsCount/byTimeSpan/{timeSpanId}  

The `timeSpanId` can assume the following values:
* **hod**: hour of day, from 0 to 23
* **dow**: day of week, from 1 to 7, where 1 means Monday
* **dom**: day of month, from 1 to 31

#### Number of impressions by US State
GET http://localhost:8080/api/impressionsCount/byUsState
# # Build stage
# FROM maven:3.8.5-openjdk-17 AS build
# COPY . .
# RUN mvn clean package -DskipTests
# # Run stage
# FROM openjdk:17.0.1-jdk-slim
# COPY --from=build /target/demo-0.0.1-SNAPSHOT.jar demo.jar
# EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "demo.jar"]

# # FROM maven:3.8.5-openjdk-17 AS build
# # COPY . .
# # RUN mvn clean package -DskipTests

# # FROM openjdk:17.0.1-jdk-slim
# # COPY --from=build /target/demo-0.0.1-SNAPSHOT.jar demo.jar

# # EXPOSE 8080
# # ENTRYPOINT ["java", "-jar", "demo.jar"]

# # Build stage
# FROM maven:3.8.5-openjdk-17 AS build
# COPY . .
# RUN mvn clean package -DskipTests
# # Run stage
# FROM openjdk:17.0.1-jdk-slim
# COPY --from=build /target/demo-0.0.1-SNAPSHOT.jar demo.jar
# EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "demo.jar"]

# FROM openjdk:8-jdk-alpine
# ARG JAR_FILE=target/*.jar
# COPY ${JAR_FILE} app.jar
# ENTRYPOINT ["java","-jar","/app.jar"]

# FROM alpine/java:21-jdk

# # Create user to run app as (instead of root)
# RUN addgroup -S app && adduser -S app -G app

# # Use user "app"
# USER app

# # copy the jar file into the docker image
# COPY target/*.jar app.jar

# # Run the jar file 
# ENTRYPOINT [ "java","-jar","/app.jar"]

# Start with a base image containing Java 21
FROM eclipse-temurin:21-jdk-alpine as build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven/Gradle wrapper and the build script
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./

# Copy the project source
COPY src ./src

# Package the application
RUN ./mvnw package -DskipTests

# Use a minimal base image for the final stage
FROM eclipse-temurin:21-jre-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the packaged jar file from the build stage
COPY --from=build /app/target/drone_app-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]
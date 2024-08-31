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
COPY --from=build /app/target/ServerMonitor-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]
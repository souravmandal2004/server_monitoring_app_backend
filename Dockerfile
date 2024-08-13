# # Build stage
# FROM maven:3.8.5-openjdk-21 AS build
# WORKDIR /app
# COPY . .
# RUN mvn clean package -DskipTests

# # Run stage
# FROM openjdk:21-jdk-slim
# WORKDIR /app
# COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar demo.jar

# EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "demo.jar"]

FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/demo-0.0.1-SNAPSHOT.jar demo.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "demo.jar"]
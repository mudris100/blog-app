# Start from a base image with JRE 21
FROM docker.io/library/eclipse-temurin:21-jre-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the application's JAR file into the container
COPY target/*.jar app.jar

# Expose the port Spring Boot app runs on
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
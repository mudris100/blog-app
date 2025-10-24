# Start from a base image with JDK 17
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the application's JAR file into the container
COPY target/blog-app-0.0.1-SNAPSHOT.jar app.jar

# Expose the port Spring Boot app runs on
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
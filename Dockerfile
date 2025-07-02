# Use an official OpenJDK image as the base
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the jar from target to the container
COPY target/*.jar app.jar

# Expose the port Render will use
EXPOSE 8080

# Set environment variables if needed
ENV JAVA_OPTS=""

# Run the jar file
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

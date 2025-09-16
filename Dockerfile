# Build stage
FROM amazoncorretto:21-alpine-jdk AS build

WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Copy config files for code quality tools
COPY config ./config

# Copy source code
COPY src ./src

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew build -x test

# Runtime stage
FROM amazoncorretto:21-alpine

WORKDIR /app

# Install curl for health check (using apk for Alpine)
RUN apk update && apk add --no-cache curl

# Copy the built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create non-root user for security
RUN adduser -D -s /bin/sh appuser
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

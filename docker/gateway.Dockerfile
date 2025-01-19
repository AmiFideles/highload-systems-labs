# Stage 1: Build
FROM maven:3.9.9-amazoncorretto-21-alpine AS build

WORKDIR /app

# Copy only the POM files initially to leverage Maven's dependency caching
COPY . .
# Resolve dependencies for the specific modules (gateway and its dependencies)
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -pl gateway,common,user-service-client -am

# Copy the actual source code for the required modules

# Build the gateway module (this will also build common and user-service-client because of dependency resolution)
RUN --mount=type=cache,target=/root/.m2 mvn clean package -pl gateway -am -DskipTests

# Stage 2: Runtime
FROM amazoncorretto:21-alpine

RUN apk --no-cache add curl

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/gateway/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Set the entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]

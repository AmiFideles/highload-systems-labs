FROM maven:3.9.9-amazoncorretto-21-alpine AS build

WORKDIR /app
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn clean install -DskipTests

WORKDIR /app/user-service
RUN --mount=type=cache,target=/root/.m2 mvn verify clean --fail-never
COPY user-service/src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

FROM amazoncorretto:21-alpine

WORKDIR /app
COPY --from=build /app/user-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM maven:3.9.9-amazoncorretto-21-alpine AS build

WORKDIR /app

COPY gateway/pom.xml gateway/pom.xml
COPY common common
COPY user-service-client user-service-client
COPY pom.xml .

WORKDIR /app/common
RUN --mount=type=cache,target=/root/.m2 mvn clean install -DskipTests

WORKDIR /app/user-service-client
RUN --mount=type=cache,target=/root/.m2 mvn clean install -DskipTests



WORKDIR /app/gateway
RUN --mount=type=cache,target=/root/.m2 mvn verify clean --fail-never
COPY gateway/src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

FROM amazoncorretto:21-alpine

WORKDIR /app
COPY --from=build /app/gateway/target/*.jar app.jar
EXPOSE 8095
ENTRYPOINT ["java", "-jar", "app.jar"]
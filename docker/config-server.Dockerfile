FROM maven:3.9.9-amazoncorretto-21-alpine AS build
WORKDIR /app
COPY config-server/pom.xml config-server/pom.xml
COPY pom.xml .

WORKDIR /app/config-server
RUN --mount=type=cache,target=/root/.m2 mvn verify clean --fail-never
COPY config-server/src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

FROM amazoncorretto:21-alpine

RUN apk --no-cache add curl

WORKDIR /app
COPY --from=build /app/config-server/target/*.jar app.jar
COPY config-server/config ./config
EXPOSE 8800
ENTRYPOINT ["java", "-jar", "app.jar"]
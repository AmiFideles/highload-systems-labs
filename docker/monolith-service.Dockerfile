FROM maven:3.9.9-amazoncorretto-21-alpine AS build
WORKDIR /app
COPY monolith/pom.xml monolith/pom.xml
COPY pom.xml .

WORKDIR /app/monolith
RUN --mount=type=cache,target=/root/.m2 mvn verify clean --fail-never
COPY monolith/src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

FROM amazoncorretto:21-alpine

RUN apk --no-cache add curl

WORKDIR /app
COPY --from=build /app/monolith/target/*.jar app.jar
EXPOSE 8099
ENTRYPOINT ["java", "-jar", "app.jar"]

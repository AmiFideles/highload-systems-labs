FROM maven:3.9.9-amazoncorretto-21-alpine AS build

WORKDIR /app
COPY . .

RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -pl user-service,common,reactive-security-module -am

RUN --mount=type=cache,target=/root/.m2 mvn clean package -pl user-service -am -DskipTests

FROM amazoncorretto:21-alpine

RUN apk --no-cache add curl

WORKDIR /app

COPY --from=build /app/user-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

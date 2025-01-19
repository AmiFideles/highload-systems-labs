FROM maven:3.9.9-amazoncorretto-21-alpine AS build

WORKDIR /app
COPY . .

RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -pl review-service,common,reactive-security-module,reactive-user-service-client,market-service-reactive-client -am

RUN --mount=type=cache,target=/root/.m2 mvn clean package -pl review-service -am -DskipTests

FROM amazoncorretto:21-alpine

RUN apk --no-cache add curl

WORKDIR /app

COPY --from=build /app/review-service/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]

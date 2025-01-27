FROM maven:3.9.9-amazoncorretto-21-alpine AS build

WORKDIR /app

COPY . .

RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -pl authentication-service,common -am

RUN --mount=type=cache,target=/root/.m2 mvn clean package -pl authentication-service -am -DskipTests

FROM amazoncorretto:21-alpine

RUN apk --no-cache add curl

WORKDIR /app

COPY --from=build /app/authentication-service/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]

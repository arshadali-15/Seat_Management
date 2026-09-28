FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw   dependency:go-offline -B

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]
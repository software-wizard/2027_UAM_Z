FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /src/target/chess.jar /app/chess.jar
EXPOSE 8080 5005
ENTRYPOINT ["java", "-jar", "/app/chess.jar"]

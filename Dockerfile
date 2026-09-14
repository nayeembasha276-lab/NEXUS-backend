FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre

WORKDIR /app

RUN useradd --system --uid 10001 nexus

COPY --from=build /app/target/*.jar app.jar

RUN chown nexus:nexus app.jar

USER nexus

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
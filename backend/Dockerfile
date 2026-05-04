# Multi-stage Docker build for the Spring Boot backend
# Stage 1: build the .jar with Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Cache dependencies first
COPY pom.xml ./
RUN mvn -B -q -e -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -B -q -e -DskipTests package

# Stage 2: small runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the fat jar from the build stage
COPY --from=build /workspace/target/portfolio-backend-*.jar app.jar

# Render injects PORT — Spring will pick it up via ${PORT:8080}
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "/app/app.jar"]

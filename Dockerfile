# Stage 1: Build
FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# El punto indica que se copie dentro de /app
COPY --from=build /app/target/*.jar ./app.jar
EXPOSE 8080
# Ejecutamos app.jar (relativo a WORKDIR)
ENTRYPOINT ["java", "-jar", "app.jar"]
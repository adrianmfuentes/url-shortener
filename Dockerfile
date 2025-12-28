# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Usamos ARG para detectar la arquitectura
ARG TARGETARCH

# Descarga el binario correcto de Tailwind según la arquitectura
RUN if [ "$TARGETARCH" = "arm64" ]; then \
        curl -sLO https://github.com/tailwindlabs/tailwindcss/releases/latest/download/tailwindcss-linux-arm64; \
        mv tailwindcss-linux-arm64 /usr/local/bin/tailwindcss; \
    else \
        curl -sLO https://github.com/tailwindlabs/tailwindcss/releases/latest/download/tailwindcss-linux-x64; \
        mv tailwindcss-linux-x64 /usr/local/bin/tailwindcss; \
    fi && chmod +x /usr/local/bin/tailwindcss

COPY . .

# Construir el JAR
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
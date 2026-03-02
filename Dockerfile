# ETAPA 1: Compilación (Indispensable para que funcione el COPY --from=build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Aprovechamos la caché de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
# Instalamos certificados para conexiones seguras (necesario para Supabase/SSL)
RUN apk add --no-cache ca-certificates
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar
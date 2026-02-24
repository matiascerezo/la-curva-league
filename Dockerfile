# ETAPA 1: Compilación (Maven)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copiamos los archivos de configuración y el código fuente
COPY pom.xml .
COPY src ./src

# Ejecutamos la compilación dentro del servidor de Railway
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución (Java)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Traemos el archivo .jar que se fabricó en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto de Spring Boot
EXPOSE 8080

# Arrancamos la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
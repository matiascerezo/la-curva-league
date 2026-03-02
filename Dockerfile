FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# El wildcard *.jar puede ser lento si hay varios archivos, mejor sé específico
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Usa esta sintaxis para que el shell expanda las variables de entorno
ENTRYPOINT java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY ./target/sensor-range-provider-0.0.1-SNAPSHOT.jar ./appl.jar
ENV CONFIG_SERVER_HOST=config-server
CMD ["java", "-jar", "/app/appl.jar"]
FROM openjdk:17-slim
VOLUME /tmp
COPY target/syntheticapi-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources /app/resources
ENTRYPOINT ["java","-jar","/app.jar"]
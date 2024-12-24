FROM openjdk:17-slim
VOLUME /tmp
COPY target/syntheticapi-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application.properties /config/application.properties
ENV SPRING_CONFIG_LOCATION=classpath:/application.properties,/config/application.properties
ENTRYPOINT ["java","-jar","/app.jar"]
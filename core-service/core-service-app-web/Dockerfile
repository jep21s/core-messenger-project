FROM eclipse-temurin:21-jre-alpine

COPY core-service-app-web-all.jar /opt/core-service/

EXPOSE 8080

WORKDIR /opt/core-service/

ENTRYPOINT ["java", "-jar", "core-service-app-web-all.jar"]
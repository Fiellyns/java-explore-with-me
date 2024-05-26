FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar emw-stats/emw-stats-service.jar
ENTRYPOINT ["java", "-jar", "emw-stats/emw-stats-service.jar"]
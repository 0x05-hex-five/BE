FROM openjdk:21
WORKDIR /app

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.config.location=/config/application.properties"]
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY ./target/auth-0.0.1-SNAPSHOT.jar /app

EXPOSE 8080

CMD ["java", "-jar", "-Dspring.profiles.active=prod", "auth-0.0.1-SNAPSHOT.jar"]
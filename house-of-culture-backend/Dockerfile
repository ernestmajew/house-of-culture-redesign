FROM openjdk:17-alpine
WORKDIR /app
COPY build/libs/house-of-culture-backend-0.0.1-SNAPSHOT.jar ./app.jar
COPY sample-images ./sample-images
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]

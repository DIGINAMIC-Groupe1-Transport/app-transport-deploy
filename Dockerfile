FROM maven:3.9-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jre-slim
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace/app
COPY pom.xml .
COPY src src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

FROM openjdk:21-jdk as builder

WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean compile -DskipTests

FROM openjdk:21-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=dev
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
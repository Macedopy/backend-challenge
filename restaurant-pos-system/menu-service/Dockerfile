FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle
RUN chmod +x gradlew gradlew.bat
RUN ./gradlew dependencies --no-daemon
COPY src ./src
RUN ./gradlew bootJar --no-daemon
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon
RUN cp build/libs/AiShortForm-0.0.1-SNAPSHOT.jar ./app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

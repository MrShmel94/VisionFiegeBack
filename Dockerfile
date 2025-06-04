FROM eclipse-temurin:21-jdk as build

WORKDIR /app

COPY . .

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS=""

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
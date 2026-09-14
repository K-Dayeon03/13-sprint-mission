# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle

RUN chmod +x gradlew
RUN --mount=type=cache,target=/root/.gradle ./gradlew dependencies --no-daemon

COPY src ./src

RUN --mount=type=cache,target=/root/.gradle ./gradlew clean bootJar --no-daemon \
    && cp build/libs/13-sprint-mission-1.2-M8.jar /tmp/discodeit-1.2-M8.jar

FROM eclipse-temurin:17-jre-jammy AS runtime

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

WORKDIR /app

COPY --from=builder /tmp/discodeit-1.2-M8.jar /app/discodeit-1.2-M8.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar /app/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]

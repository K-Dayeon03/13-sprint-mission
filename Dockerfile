# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle

RUN chmod +x gradlew
RUN --mount=type=cache,target=/root/.gradle ./gradlew dependencies --no-daemon

COPY src ./src

RUN --mount=type=cache,target=/root/.gradle ./gradlew clean bootJar --no-daemon \
    && cp build/libs/*.jar /tmp/discodeit.jar

FROM eclipse-temurin:17-jre-jammy AS runtime

ENV PROJECT_NAME=discodeit
ENV JVM_OPTS=""

WORKDIR /app

COPY --from=builder /tmp/discodeit.jar /app/discodeit.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar /app/discodeit.jar"]

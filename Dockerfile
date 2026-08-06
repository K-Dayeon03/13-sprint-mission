FROM amazoncorretto:17@sha256:b5735ca096df8968438759e535736da85aa33d4024ddb4e3cc0e42341de3984c AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew \
    && ./gradlew clean bootJar \
    && cp build/libs/13-sprint-mission-1.2-M8.jar discodeit-1.2-M8.jar

FROM amazoncorretto:17@sha256:b5735ca096df8968438759e535736da85aa33d4024ddb4e3cc0e42341de3984c

WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

COPY --from=builder /app/discodeit-1.2-M8.jar /app/discodeit-1.2-M8.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar /app/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]

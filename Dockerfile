
ARG JAVA_VERSION=25

FROM eclipse-temurin:${JAVA_VERSION}-jdk-alpine AS builder

WORKDIR /builder

COPY .mvn/ .mvn/

COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -q

COPY src ./src

ARG SENTRY_AUTH_TOKEN

ENV SENTRY_AUTH_TOKEN=${SENTRY_AUTH_TOKEN}

RUN ./mvnw package -DskipTests -q

RUN java -Djarmode=layertools -jar target/*.jar extract --destination extracted

FROM eclipse-temurin:${JAVA_VERSION}-jre-alpine AS runtime

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

WORKDIR /application

COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
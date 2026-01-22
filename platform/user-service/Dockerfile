FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./

RUN chmod +x ./gradlew

COPY src ./src

RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8089

ARG CI_COMMIT_SHA
ARG CI_PIPELINE_CREATED_AT
ARG CI_COMMIT_REF_NAME
ARG CI_COMMIT_TAG
ARG CI_PIPELINE_ID

LABEL org.opencontainers.image.title="Moko: User Service" \
      org.opencontainers.image.description="Service that manages user profiles for the Moko platform." \
      org.opencontainers.image.version="${CI_COMMIT_TAG:-latest}" \
      org.opencontainers.image.url="https://gitlab.com/kdg-ti/integratieproject-j3/teams-25-26/team22/user-service" \
      org.opencontainers.image.revision="$CI_COMMIT_SHA" \
      org.opencontainers.image.created="$CI_PIPELINE_CREATED_AT" \
      build_number="$CI_PIPELINE_ID" \
      authors="Lee, Kaj, Mathias, Matti"

ENTRYPOINT ["java", "-jar", "app.jar"]
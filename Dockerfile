FROM gradle:7.5-jdk11-alpine as builder
WORKDIR /build

COPY build.gradle settings.gradle /build/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

COPY . /build
RUN gradle build -x test --parallel


FROM openjdk:11.0-slim
WORKDIR /app
COPY --from=builder /build/build/libs/order-api-0.0.1-SNAPSHOT.jar order-app.jar


EXPOSE 8080
ENTRYPOINT [                     \
  "java",                        \
  "-jar",                        \
  "-Dspring.profiles.active=prod",  \
  "order-app.jar"  \
]
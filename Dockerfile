FROM adoptopenjdk/openjdk11
ARG JAR_FILE_PATH=build/libs/learncha-app-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE_PATH} learncha-app.jar

EXPOSE 8080
ENTRYPOINT [                     \
  "java",                        \
  "-jar",                        \
  "-Dspring.profiles.active=prod",  \
  "learncha-app.jar"  \
]
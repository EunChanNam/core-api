ARG JAR_FILE_PATH=build/libs/learcha-app-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE_PATH} learncha-app.jar

EXPOSE 8080
ENTRYPOINT [                     \
  "java",                        \
  "-jar",                        \
  "-Dspring.profiles.active=default",  \
  "learncha-app.jar"  \
]
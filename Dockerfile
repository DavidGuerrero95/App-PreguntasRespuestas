FROM openjdk:12
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} PreguntasRespuestas.jar
ENTRYPOINT ["java","-jar","/PreguntasRespuestas.jar"]
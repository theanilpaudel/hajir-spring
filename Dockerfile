FROM openjdk:8
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} hajir.jar
ENTRYPOINT ["java","-jar","/hajir.jar"]
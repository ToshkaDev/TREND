# Alpine Linux with OpenJDK JRE
FROM openjdk:8-jre-alpine

RUN mkdir /app

COPY target/proto-tree-0.0.1-SNAPSHOT.jar /app/proto-tree.jar

CMD ["/usr/bin/java", "-jar", "/app/proto-tree.jar"]
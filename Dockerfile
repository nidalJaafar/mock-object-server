FROM openjdk:17.0.1

EXPOSE 8080

ADD build/libs/* app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
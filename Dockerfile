FROM openjdk:8-alpine

COPY target/uberjar/gravie-developer-test.jar /gravie-developer-test/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/gravie-developer-test/app.jar"]

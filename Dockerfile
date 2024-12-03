FROM openjdk:17-jdk-slim
COPY . /app
WORKDIR /app

RUN ./mvnw clean package

COPY target/interview-0.0.1.jar /interview.jar

EXPOSE 8082

CMD ["java", "-jar", "/interview.jar"]

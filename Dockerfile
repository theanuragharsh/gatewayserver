FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} gatewayserver.jar
EXPOSE 8072
CMD ["java", "-jar", "gatewayserver.jar"]


# build and run docker image
#1. cd /mnt/d/Bank\ Service/customer-service
#2. docker build -t customer-service .
#3. docker run -p 8082:8082 customer-service
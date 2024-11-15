FROM OPENJDK:17-jre
WORKDIR /opt/app
ADD target/cloud-api-1.0-SNAPSHOT.jar /opt/app/app.jar
EXPOSE 80
CMD ["java","-jar","app.jar"]
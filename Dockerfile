## Docker file ###
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY Backend /app/Backend
RUN mvn install -DskipTests=true -f /app/Backend/pom.xml

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/Backend/target/ShopApp-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build /app/Backend/uploads uploads
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"]

#FROM openjdk:17-alpine
#
#ARG FILE_JAR=target/*.jar
#
#ADD ${FILE_JAR} app.jar
#
#EXPOSE 8080
#
#ENTRYPOINT ["java", "-jar", "app.jar"]

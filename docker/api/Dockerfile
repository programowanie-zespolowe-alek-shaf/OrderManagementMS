FROM maven:3.6.3-openjdk-11
WORKDIR app
COPY om-application ./om-application
COPY om-mysql ./om-mysql
COPY pom.xml ./

ENV MYSQL_DB_HOST name
ENV MYSQL_DB_PORT 3306
ENV MYSQL_DATABASE order
ENV MYSQL_DB_USERNAME order
ENV MYSQL_DB_PASSWORD order
ENV EUREKA_HOST name
ENV EUREKA_PORT 8761

RUN mvn clean install -DskipTests -P prod
RUN mv om-application/target/*.jar app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","app.jar"]
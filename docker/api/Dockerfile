FROM maven:latest
WORKDIR app
COPY om-application ./om-application
COPY om-mysql ./om-mysql
COPY pom.xml ./
RUN mvn clean install -DskipTests -P prod
RUN mv om-application/target/*.jar app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","app.jar"]
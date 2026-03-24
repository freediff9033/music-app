# STAGE 1: Build using Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /usr/src/app

# Copy everything
COPY . .

# Build the app
RUN mvn clean package -DskipTests

# STAGE 2: Run using Java 21+ runtime
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:latest
COPY --from=build /usr/src/app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build /usr/src/app/target/quarkus-app/*.jar /deployments/
COPY --from=build /usr/src/app/target/quarkus-app/app/ /deployments/app/
COPY --from=build /usr/src/app/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]
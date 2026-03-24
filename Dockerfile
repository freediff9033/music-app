# STAGE 1: Build
FROM maven:3.8.6-openjdk-18 AS build

# This now copies the CONTENTS of code-with-quarkus
WORKDIR /usr/src/app
COPY . .

# Let's be 100% sure we see the pom.xml
RUN ls -la

RUN mvn clean package -DskipTests

# STAGE 2: Run
FROM registry.access.redhat.com/ubi9/openjdk-25-runtime:1.24
COPY --from=build /usr/src/app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build /usr/src/app/target/quarkus-app/*.jar /deployments/
COPY --from=build /usr/src/app/target/quarkus-app/app/ /deployments/app/
COPY --from=build /usr/src/app/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]
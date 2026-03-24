# STAGE 1: The "Kitchen" (Build the app)
FROM maven:3.8.6-openjdk-18 AS build
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mvn clean package -DskipTests

# STAGE 2: The "Dining Room" (Run the app)
# This uses the official Quarkus runtime image 
FROM registry.access.redhat.com/ubi9/openjdk-25-runtime:1.24

# Set the language 
ENV LANGUAGE='en_US:en'

# Copy the built files from Stage 1 into the runner 
COPY --from=build /usr/src/app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build /usr/src/app/target/quarkus-app/*.jar /deployments/
COPY --from=build /usr/src/app/target/quarkus-app/app/ /deployments/app/
COPY --from=build /usr/src/app/target/quarkus-app/quarkus/ /deployments/quarkus/

# Open the door for web traffic 
EXPOSE 8080
USER 185

# Tell Java how to start the Quarkus app 
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

# The command that actually starts the music player 
ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]
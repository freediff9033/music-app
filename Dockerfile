# STAGE 1: Build
FROM maven:3.8.6-openjdk-18 AS build

WORKDIR /usr/src/app

# Copy EVERYTHING from your GitHub
COPY . .

# This finds the pom.xml wherever it is and runs the build there
RUN POM_DIR=$(find . -name "pom.xml" -printf '%h' -quit) && \
    echo "Found POM in: $POM_DIR" && \
    cd "$POM_DIR" && \
    mvn clean package -DskipTests && \
    mkdir -p /usr/src/app/target && \
    cp -r target/quarkus-app /usr/src/app/target/
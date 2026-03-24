FROM maven:3.8.6-openjdk-18 AS build
WORKDIR /usr/src/app
COPY . .
# This will now show your pom.xml in the logs!
RUN ls -la
RUN mvn clean package -DskipTests

# ... (Keep the rest of Stage 2 as it was)
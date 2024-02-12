FROM ubuntu:latest

# Install necessary dependencies
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y \
    wget \
    && rm -rf /var/lib/apt/lists/*

# Install Amazon Corretto 17
FROM amazoncorretto:17.0.10

# Set the working directory
WORKDIR /app

# Copy your application JAR file
COPY build/libs/*.jar app.jar
ENV DISPLAY :10

# Run the Java application with headless mode
CMD ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]

#FROM amazoncorretto:17.0.10
#VOLUME /tmp
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]


# Use official openjdk image as base image
FROM openjdk:11-jdk-slim

# Set environment variables (Optional: for configuration or custom settings)
ENV JETTY_HOME=/opt/jetty
ENV JETTY_BASE=/opt/jetty-base
ENV JETTY_PORT=8080

# Install wget and download Jetty
RUN apt-get update && apt-get install -y wget

# Download Jetty
RUN wget -q -O /tmp/jetty.tar.gz https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-home/11.0.15/jetty-home-11.0.15.tar.gz

# Create /opt directory and unpack Jetty
RUN mkdir -p /opt && \
    tar -xvzf /tmp/jetty.tar.gz -C /opt && \
    rm /tmp/jetty.tar.gz && \
    mv /opt/jetty-home-11.0.15 /opt/jetty && \
    ln -s /opt/jetty/bin/jetty.sh /usr/local/bin/jetty

# Create the logs directory
RUN mkdir -p /opt/jetty-base/logs && \
    chmod 777 /opt/jetty-base/logs

# Expose the port Jetty will run on
EXPOSE 8080

#Copy jetty-base directory to container
COPY jetty-base /opt/jetty-base

# Copy WAR file into Jetty webapps directory
COPY target/AIEDproject-1.0-SNAPSHOT.war /opt/jetty-base/webapps/ROOT.war

# Start Jetty
CMD ["java", "-Djetty.base=/opt/jetty-base", "-jar", "/opt/jetty/start.jar"]


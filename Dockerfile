# Use Jetty image
FROM jetty:12-jre17
RUN ls -l /usr/local/jetty

# set deploment directory
WORKDIR /var/lib/jetty/webapps/

# Create the logs directory to avoid the IOException
RUN mkdir -p /var/lib/jetty/jetty_base/logs

#copy WAR file to webapps directory
COPY target/AIEDproject-1.0-SNAPSHOT.war /var/lib/jetty/webapps/ROOT.war
COPY ./jetty-base /var/lib/jetty/base

#Expose port 8080(default jetty port)
EXPOSE 8080

# Command to run Jetty
CMD ["java", "-Djetty.base=/var/lib/jetty/jetty-base", "-jar", "/usr/local/jetty/start.jar"]



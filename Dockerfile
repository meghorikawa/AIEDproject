# Use Jetty image
FROM jetty:11-jre17

# set deploment directory
#WORKDIR /var/lib/jetty/webapps/

#copy WAR file to webapps directory
COPY target/AIEDproject-1.0-SNAPSHOT.war /var/lib/jetty/webapps/ROOT.war

#Expose port 8080(default jetty port)
EXPOSE 8080
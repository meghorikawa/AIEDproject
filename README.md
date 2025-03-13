# AIEDproject
Our final project for winter '24 seminar of Education system design with AI. We developed a servlet which could extend the ARES I-CALL system which uses an LLM to generate practice exercises for grammar forms students encounter in texts.  

## Running the Servlet Locally
To run the servlet locally you will need your own docker-compose.yml file in the main directory of the project. **To protect your API key this file should be added to the .gitignore file of your repository**. The structure of  **docker-compose.yml** file should look like this. 
```yaml
services:
  aiedproject:
    container_name: aied-container
    build: .
    ports:
      - "8080:8080"
    environment:
      - GROQ_API_KEY=your api key here
```

to build and then deploy the container, navigate to the project directory in your terminal and run the following command:

```yaml
docker-compose up -d
```
to stop the container run the following command:
```yaml
docker-compose down
```

## Recompiling the project after changes
If changes have been made to the code in the project you will need to recompile it by runnnig the following command in the main directory of the project: 
```yaml
mvn clean package
```
from here you will then redeploy the container as given in the instructions above.


# Cases Integrator
*Spring Boot REST API for Cases management<br>

### Get Started

* Clone this repository using either SSH or HTTP on https://github.com/claytonrm/cases-integrator
* Install requirements

### Requirements
- [Java 11](https://www.oracle.com/java/technologies/javase-downloads.html#JDK11)
- [Lombok](https://projectlombok.org/download)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com)

### Running App

* Open the project on your IDE and run CasesIntegratorApplication main class or in your bash (project root) `mvn spring-boot:run` (Ctrl + C to stop running it)
* Check the URL `http://localhost:8080/swagger-ui.html`

#### Available operations
* You can try it out each of operations available in above URL or via [Postman](https://www.getpostman.com/)

### Running Tests
```shell
mvn test
```

### Sonar

You can check loads of stats about main codes with Sonar.

* In your terminal type `docker pull sonarqube` to download sonar image. <br>
* Run `docker run -d --name sonarqube -p 9000:9000 -p 9092:9092 sonarqube` to create a docker container <br>

Now you are able to generate test files and check your code out.
```shell script
mvn clean package sonar:sonar
```

After that, you can open http://localhost:9000/projects
# Cases Integrator
*Spring Boot REST API for Cases management*<br>

### Get Started

* Clone this repository using either SSH or HTTP on https://github.com/claytonrm/cases-integrator
* Install requirements
* Create a new project on [GCP](https://cloud.google.com/?&utm_source=google&utm_medium=cpc&utm_campaign=latam-BR-all-pt-dr-bkws-all-all-trial-e-dr-1008075-LUAC0010101&utm_content=text-ad-none-none-DEV_c-CRE_427606926410-ADGP_BKWS+%7C+Multi+~+Google+Cloud+Platform-KWID_43700047045899986-kwd-301173107504-userloc_1001706&utm_term=KW_google%20cloud%20platform-ST_Google+Cloud+Platform&gclid=CjwKCAjw1v_0BRAkEiwALFkj5t0mGLIDTpc1xX_A3Hp_PtE4TffDUJaq80BoD13UJf38WMyJEu4aGhoCtk8QAvD_BwE&gclsrc=aw.ds) account and download your own credential key/file (json)

### Requirements
- [Java 11](https://www.oracle.com/java/technologies/javase-downloads.html#JDK11)
- [Firestore](https://cloud.google.com/firestore/?&utm_source=google&utm_medium=cpc&utm_campaign=latam-BR-all-pt-dr-bkws-all-all-trial-b-dr-1008075-LUAC0008670&utm_content=text-ad-none-none-DEV_c-CRE_429266674480-ADGP_BKWS+%7C+Multi+~+Storage+%7C+Firestore-KWID_43700046780193830-kwd-810649847511-userloc_1001706&utm_term=KW_%2Bgoogle%20%2Bfirestore-ST_%2BGoogle+%2BFirestore&gclid=CjwKCAjw1v_0BRAkEiwALFkj5sHrYsIzEXCcf1JicUq9lNMF9MzahNnCtbYGJo2tOtyb9BND78ZWkxoCAAgQAvD_BwE&gclsrc=aw.ds)
- [Lombok](https://projectlombok.org/download)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com)

### Setup

* Override your ```application.properties``` as following:

```yml
spring.cloud.gcp.firestore.credentials.location=file:/<replace_by_your_path_file>.json
```

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

# PetCare

Spring Boot application scaffolded with Java 17, Maven, and dependencies: Spring Web, Spring Data JPA, Spring Security, MySQL Driver, Lombok.

## Prerequisites
- Java 17+
- Maven 3.9+
- MySQL (optional until you use JPA)

## Configure Database
Edit `src/main/resources/application.properties` with your MySQL credentials:

```
spring.datasource.url=jdbc:mysql://localhost:3306/petcare?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=changeme
```

## Run Locally
Build and run the app:

```bash
mvn -DskipTests package
mvn spring-boot:run
```

Then open: http://localhost:8080/api/health

## Notes
- Security allows `/api/health` without auth; other endpoints require basic auth.
- Add your entities and repositories under `com.petcare` packages.
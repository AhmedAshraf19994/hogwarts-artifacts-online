# Hogwarts Artifacts API

## Spring Boot backend for managing magical artifacts with secure JWT authentication and Redis-based token revocation.

A stateless backend system that provides authentication, authorization, and artifact management.
It uses JWT for authentication and Redis to handle token revocation (logout problem).

🔗 [Swagger API Docs](https://app.swaggerhub.com/apis/ahmed-7ee/HogwartsArtifactsOnline/1.0.0)

* JWT Authentication (OAuth2 Resource Server)
* Role-based access control (USER / ADMIN)
* Custom authorization logic per user
* Redis-based token whitelist (revocation)
* Stateless architecture (no sessions)
* Integration testing with Testcontainers

### Tech Stack
* Java 25
* Spring Boot 4
* Spring Security
* Spring Data JPA
* PostgreSQL / H2
* Redis
* Testcontainers
* Maven
 
## Run Locally
1. ### Start Required Containers
* docker compose up -d
2. ### Start Required Containers
* ./mvnw spring-boot:run

## Testing
### Run all tests
./mvnw test

### Integration Tests
* Uses Testcontainers
* Redis starts automatically
* No manual setup required

### Notes
* Only one active token per user
* Logging in again invalidates previous token
* Redis is required for token validation

### Author
Ahmed Ashraf


# Smart Pet Care Application

A full‑stack application to manage pet healthcare: registrations, secure logins, appointments, and essentials — designed for pet owners, veterinary doctors, and administrators.

**Backend:** Spring Boot (Java 17) • Spring Web • Spring Data JPA • Spring Security • MySQL • Lombok

**Frontend:** React • React Router • Axios • Create React App

---

**Table of Contents**
- Overview
- Architecture
- Tech Stack
- Features
- Roles & Access Control
- Project Structure
- Getting Started
  - Prerequisites
  - Backend Setup
  - Frontend Setup
- Key API Endpoints
- Development Notes (CORS, Sessions, Passwords)
- Future Enhancements

---

## Overview
Smart Pet Care helps pet owners and veterinary doctors collaborate through secure, session‑based authentication, with role‑aware screens and APIs. The system uses a React frontend talking to a Spring Boot backend with a MySQL database.

## Architecture
- React SPA (port 3000) communicates with Spring Boot (port 8080).
- Session‑based authentication with `JSESSIONID` cookie. Axios is configured to send credentials automatically.
- CORS allows the frontend origin and credentials.
- Persistence via JPA to MySQL.

## Tech Stack
- Backend: Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security, Lombok, MySQL
- Frontend: React, React Router v6, Axios, Create React App
- Build Tools: Maven (backend), npm (frontend)

## Features
- Authentication & Authorization
  - Session‑based login with `BCrypt` password hashing
  - Role‑based endpoint protection (Owner/Doctor/Admin)
- User Management
  - Register Pet Owner or Veterinary Doctor
  - Login/Logout; fetch current session user
- Core Pages
  - Landing (Hero, Features, How‑it‑works, CTA)
  - Register, Login with role‑based redirects
- Developer‑friendly setup & CORS preconfigured for local dev

## Roles & Access Control
- Pet Owner: access to `/pet-owner/**`
- Veterinary Doctor: access to `/doctor/**`
- Admin: access to `/admin/**`
- Public: `/auth/**`, `/api/health`

## Project Structure
- Backend: [petcare/](petcare)
  - Config: [petcare/src/main/java/com/petcare/config](petcare/src/main/java/com/petcare/config)
  - Entities: [petcare/src/main/java/com/petcare/entity](petcare/src/main/java/com/petcare/entity)
  - Repository: [petcare/src/main/java/com/petcare/repository](petcare/src/main/java/com/petcare/repository)
  - Services: [petcare/src/main/java/com/petcare/service](petcare/src/main/java/com/petcare/service)
  - Controllers: [petcare/src/main/java/com/petcare/controller](petcare/src/main/java/com/petcare/controller)
  - Security: [petcare/src/main/java/com/petcare/security](petcare/src/main/java/com/petcare/security)
  - Properties: [petcare/src/main/resources/application.properties](petcare/src/main/resources/application.properties)
- Frontend: [petcare-frontend/](petcare-frontend)
  - API Client: [petcare-frontend/src/api/axios.js](petcare-frontend/src/api/axios.js)
  - Pages: [petcare-frontend/src/pages](petcare-frontend/src/pages)
  - Components: [petcare-frontend/src/components](petcare-frontend/src/components)
  - Assets: [petcare-frontend/src/assets/images](petcare-frontend/src/assets/images)

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.9+
- Node.js 18+ and npm 9+
- MySQL 8+

### Backend Setup
1) Update database credentials in [petcare/src/main/resources/application.properties](petcare/src/main/resources/application.properties):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/petcare?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=YOUR_USER
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

2) Build & run the backend:

```powershell
cd "petcare"
mvn -DskipTests package
mvn spring-boot:run
```

The API runs at http://localhost:8080.

### Frontend Setup
1) Install dependencies:

```powershell
cd "petcare-frontend"
npm install
```

2) Run the dev server:

```powershell
npm start
```

The app runs at http://localhost:3000.

## Key API Endpoints
- Health: `GET /api/health`
- Auth:
  - `POST /auth/register` (JSON: name, email, phone, password, role)
  - `POST /auth/login` (form‑urlencoded: username, password)
  - `POST /auth/logout`
- Current User: `GET /api/user/me`

## Development Notes (CORS, Sessions, Passwords)
- CORS is enabled for `http://localhost:3000` with credentials in the backend security config.
- Axios is configured with `withCredentials: true`, so the browser sends `JSESSIONID` cookie on every request.
- Passwords are stored using BCrypt. To generate a hash in Java:

```java
new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("yourPassword");
```

## Future Enhancements
- Appointments lifecycle (create/approve/reschedule/cancel) with calendar view
- Pet profiles with medical history and vaccination reminders
- Marketplace ordering and payments
- Notifications (email/SMS/push) and in‑app messaging
- File uploads for pet photos and prescriptions
- Role‑aware dashboards and analytics
- Integration tests, contract tests, and API documentation (OpenAPI/Swagger)
- Dockerization and CI/CD pipeline
- Caching, rate limiting, metrics, and distributed tracing

---

If you want, I can add a Maven Wrapper and a Docker Compose file to run MySQL + backend locally with one command.
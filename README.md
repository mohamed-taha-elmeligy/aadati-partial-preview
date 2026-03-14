# Aadati — Backend System (Partial Preview)

This repo is a partial public version of the Aadati backend. It exposes the security layer, JWT implementation, and core configurations for review. The full business logic stays private.

---

## What is Aadati?

Aadati is a habit and task tracking system. Users define things they want to do daily — the system tracks whether they did them or not, calculates their performance over time, and grades them weekly.

Think of it as a personal accountability backend.

---

## What makes it interesting technically

### Self-Healing on Startup
Every time the app starts, it runs through a sequence of checks:

1. Makes sure admin accounts and roles exist
2. Looks for gaps in calendar data and fills them
3. Finds any missing tracking records and generates them
4. Recalculates analytics to make sure stats are accurate

This means the system can recover from partial failures or bad deploys without manual intervention.

### Scheduled Jobs
- Every day at midnight: generates tracking records for all active users
- On yearly rollover: initializes the new calendar year
- On any data change: recalculates affected metrics immediately

### Security
- JWT-based stateless auth with configurable expiration
- Role-Based Access Control (ADMIN / USER)
- Device and browser detection per session
- Spring Security 6 with custom filter chain

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.5.4 |
| Language | Java 21 |
| Security | Spring Security 6 + JWT |
| Database | PostgreSQL |
| ORM | JPA / Hibernate |
| Docs | Swagger / OpenAPI |
| Code Quality | SonarQube |

---

## Project Structure

```
src/main/java/com/aadati/
├── entities/         # 13 domain entities
├── repositories/     # Data access
├── services/         # Business logic
├── controllers/      # REST API (150+ endpoints in full version)
├── initialization/   # Self-healing & bootstrap logic
├── calculation/      # Analytics engine
├── security/         # Spring Security config
├── jwt/              # JWT implementation
├── dto/              # Request & response objects
├── mapper/           # DTO mappers
└── exception/        # Global error handling
```

---

## API

Full version has 150+ endpoints across these areas:
- Auth & user management
- Habit/task management
- Categories & priorities
- Daily tracking & completion
- Analytics & weekly reports
- Calendar & scheduling

Docs available at `/swagger-ui.html` after running locally.

---

## Running Locally

**Requirements:** Java 21, PostgreSQL, Maven 3.8+

```bash
git clone https://github.com/mohamed-taha-elmeligy/aadati-partial-preview.git
cd aadati-partial-preview
```

Configure your DB in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_username
spring.datasource.password=your_password
server.port=8082
```

Then:

```bash
mvn clean install
mvn spring-boot:run
```

---

Built by [Mohamed Taha](https://github.com/mohamed-taha-elmeligy) — open to internship and junior backend opportunities.

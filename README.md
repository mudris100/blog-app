# Blog Platform

A full-stack **Java Spring Boot** blog application with a **Spring MVC + Thymeleaf** frontend and a **REST API**.
Users can register, sign in, write blog posts, and comment on others' posts.
Access is controlled through **role-based security** (`USER` and `ADMIN`).

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
  - [PostgreSQL (production profile)](#postgresql-production-profile)
  - [Redis session storage](#redis-session-storage)
  - [OAuth2 login (GitHub and Google)](#oauth2-login-github-and-google)
- [Docker](#docker)
- [API Documentation (Swagger)](#api-documentation-swagger)

---

## Features

- **Authentication & registration** with Spring Security and password hashing
- **Role-based access control** (`USER`, `ADMIN`)
- **OAuth2 / OpenID Connect login** with Google and GitHub, alongside local authentication (optional, see [OAuth2 login](#oauth2-login-github-and-google))
- **Unified user model**: OAuth2 and local users live in the same database
- **Blog posts**: create, edit, delete
- **Comments** on posts
- **Admin dashboard** (restricted to `ADMIN`)
- **REST API** endpoints, tested with Postman
- **Redis-backed HTTP sessions** for load-balancer-friendly deployments
- **Pre-seeded data** (users, posts) for testing

## Tech Stack

| Layer | Technologies |
|---|---|
| Backend | Java, Spring Boot, Spring MVC, Spring Security, OAuth2, Hibernate (JPA) |
| Frontend | Thymeleaf (server-side rendering) |
| Database | H2 (default), PostgreSQL (production-ready) |
| Session store | Redis |
| Build tool | Maven |
| Testing | JUnit, Postman |
| DevOps | Docker, Docker Compose, Jenkins (CI/CD) |
| Cloud | AWS EC2 (Ubuntu) |

---

## Quick Start

The project ships preconfigured with an **H2 in-memory database**, so you can run it immediately without installing PostgreSQL or Redis.

```bash
git clone https://github.com/mudris100/blog-app
cd blog-app
./mvnw spring-boot:run
```

Then open:

- App: <http://localhost:8080/>
- H2 console: <http://localhost:8080/h2-console>

---

## Configuration

### PostgreSQL (production profile)

For a production-like setup, set the following environment variables and activate the `prod` profile:

| Property | Value |
|---|---|
| `spring.datasource.url` | `${DB_URL}` |
| `spring.datasource.username` | `${DB_USER}` |
| `spring.datasource.password` | `${DB_PASSWORD}` |
| `spring.datasource.driver-class-name` | `org.postgresql.Driver` |
| `spring.profiles.active` | `prod` |

### Redis session storage

The application uses **Redis to store `HttpSession` data** (via Spring Session) instead of keeping sessions in the memory of a single application instance.

**What this does and does not give you:**

- The app is **not stateless**: users still have server-side sessions, and a session ID cookie identifies them.
- It **is load-balancer friendly**: because session data lives in Redis rather than in one instance's memory, any application instance can serve any request. You can run multiple instances behind a load balancer without sticky sessions, and sessions survive an application restart.

Redis is included in the Docker Compose setup (see [Docker](#docker)). When running the app outside Compose, point it at your Redis instance:

| Variable | Description |
|---|---|
| `REDIS_HOST` | Redis hostname |
| `REDIS_PORT` | Redis port (default `6379`) |

### OAuth2 login (GitHub and Google)

Users can sign in with **GitHub** or **Google** in addition to local username/password accounts. OAuth2 users are stored in the same database as local users.

OAuth2 is **disabled by default**. To enable it:

1. Set the feature flag:

   ```bash
   APP_OAUTH_ENABLED=true
   ```

2. Register an OAuth application with each provider and provide its credentials:

   | Variable | Description |
   |---|---|
   | `GITHUB_CLIENT_ID` | GitHub OAuth app client ID |
   | `GITHUB_CLIENT_SECRET` | GitHub OAuth app client secret |
   | `GOOGLE_CLIENT_ID` | Google OAuth client ID |
   | `GOOGLE_CLIENT_SECRET` | Google OAuth client secret |

3. Set the authorized redirect URI in each provider's console to match your deployment (Spring Security's default pattern is `{baseUrl}/login/oauth2/code/{registrationId}`, e.g. `http://localhost:8080/login/oauth2/code/github`).

> Never commit client secrets to the repository. Supply them through environment variables or your secret manager.

---

## Docker

The `Dockerfile` expects a **prebuilt JAR** in `/target`.

**1. Build the JAR**

```bash
./mvnw clean package
```

**2. Build the image**

```bash
docker build -t blog-app:v1 .
```

**3. Start the full stack** (Spring Boot app, PostgreSQL, and Redis):

```bash
docker compose up -d
```

---

## API Documentation (Swagger)

Swagger UI lets you explore and test the REST API endpoints.

- **URL:** <http://localhost:8080/swagger-ui/index.html>
- **Access:** `ADMIN` role only
- **Seeded admin account:** username `admin`, password `1234`

> The seeded credentials are for local development only. Change or remove them in any shared or production deployment.

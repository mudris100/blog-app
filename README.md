# Blog Platform

A full-stack **Java Spring Boot** blog application with **Spring MVC + Thymeleaf** frontend and **REST API** support.
Users can register, sign in, create blog posts, and comment on others.  
Includes **role-based access control** with `USER` and `ADMIN` roles.

---

## Features

- User authentication & registration (**Spring Security** with password hashing)
- Role-based access (`USER`, `ADMIN`)
- OAuth2 / OpenID Connect integration
Login with Google and GitHub, alongside local authentication  
- Unified user handling — OAuth2 users are stored in the same database as local users
- Create, edit, delete blog posts
- Comment system
- Admin dashboard (restricted to `ADMIN`)
- Exposed REST API endpoints (tested with Postman)
- Pre-seeded data (users, posts) for testing
- Kafka integration: publishes new post events to Kafka broker

---

## Tech Stack

- **Backend:** Java, Spring Boot, Spring MVC, Spring Security, OAuth2, Hibernate (JPA)
- **Frontend:** Thymeleaf (server-side rendering)
- **Database:** H2 (default), PostgreSQL (production-ready)
- **Build Tool:** Maven
- **Testing:** JUnit, Postman
- **DevOps:** Docker, Docker Compose, Jenkins (CI/CD)
- **Cloud Deployment:** AWS EC2 (Ubuntu)
- **Messaging:** Apache Kafka (for event-driven notifications)

---

## Kafka Integration

The project demonstrates event-driven architecture with Apache Kafka — but Kafka is optional  
and disabled in lightweight profiles (h2, prod) for easier startup. Enable Kafka (for local dev / advanced users)  
If you want to experience full event-driven flow:  
Start a Kafka cluster (for example, 3 brokers running on Ubuntu or Docker Compose).  
Use the dev profile (default) where Kafka is enabled in application.properties.  
Verify your spring.kafka.bootstrap-servers points to your Kafka nodes.

- **Producer** (Blog App) → Publishes `NewPostEvent` (JSON) whenever a new blog post is created.
- **Broker** (Kafka Cluster, 3 nodes on Ubuntu) → Stores and distributes the event across partitions.
- **Consumer** (Standalone app / local console consumer) → Reads events and deserializes them into proper Java objects.
- This setup allows the blog platform to be extended with microservices (e.g., Notification Service to send emails when
  a new post is created

## API Documentation (Swagger)

Swagger UI allows exploring and testing REST API endpoints:

URL: http://localhost:8080/swagger-ui/index.html

Access: Only available for users with ADMIN role

Use the seeded admin account (username: admin, password: 1234)

---

## Installation

### 1. Clone repository

git clone https://github.com/mudris100/blog-app

### 2. Run with default H2 database (instant setup)

The project comes preconfigured with an **H2 in-memory database**.  
This means you can start the app immediately without installing PostgreSQL:

./mvnw spring-boot:run

- http://localhost:8080/h2-console
- http://localhost:8080/

### 3. Run with PostgreSQL (production-like setup)

If you prefer using PostgreSQL, define environment variables before starting:

- spring.datasource.url=${DB_URL}
- spring.datasource.username=${DB_USER}
- spring.datasource.password=${DB_PASSWORD}
- spring.datasource.driver-class-name=org.postgresql.Driver
- spring.profiles.active=prod

### 4. Run with Docker Compose

**Dockerfile expects a prebuilt JAR in /target**

./mvnw clean package

To start both the Spring Boot app and PostgreSQL database with one command:

docker compose up -d --build






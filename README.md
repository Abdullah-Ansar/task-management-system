# 🚀 Task Management & Team Collaboration API

A production-ready RESTful Task Management API built using **Spring Boot**, following clean architecture principles and secured with **Spring Security** and **JWT Authentication**.

The application enables users to securely manage their own tasks while allowing administrators to manage users through role-based authorization. The project is fully containerized with Docker and deployed on Railway using MySQL.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue)
![Railway](https://img.shields.io/badge/Deployed-Railway-purple)
![License](https://img.shields.io/badge/License-MIT-blue)

---

# 🌐 Live Demo

## 🚀 Live Swagger UI

https://task-management-system-production-cb49.up.railway.app/swagger-ui/index.html

## 📄 OpenAPI Documentation

https://task-management-system-production-cb49.up.railway.app/v3/api-docs

---

# 📂 GitHub Repository

https://github.com/Abdullah-Ansar/task-management-system

---

# ✨ Features

## 🔐 Authentication & Security

- JWT Authentication
- Spring Security 6
- BCrypt Password Encoding
- Stateless Authentication
- Role-Based Authorization (USER / ADMIN)
- User-specific Resource Ownership
- Protected REST APIs

---

## 📋 Task Management

- Create Task
- Update Task
- Delete Task
- Get Task by ID
- Get All Tasks
- Pagination
- Sorting
- Search Tasks
- Task Ownership Validation

---

## 👨‍💼 Admin Features

- View All Users
- Manage Users
- Restricted ADMIN Endpoints

---

## 🏗 Backend Architecture

- Layered Architecture
- DTO Pattern
- Repository Pattern
- Bean Validation
- Global Exception Handling
- RESTful API Design
- Standardized API Responses

---

## 🗄 Database

- MySQL
- Spring Data JPA
- Hibernate ORM
- One-to-Many Relationships
- Many-to-One Relationships

---

## 🐳 DevOps & Deployment

- Docker
- Docker Compose
- Railway Cloud Deployment
- Environment Variables
- Production-ready Dockerfile

---

# 🛠 Tech Stack

| Category | Technologies |
|----------|--------------|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Security | Spring Security, JWT, BCrypt |
| Database | MySQL |
| ORM | Hibernate, Spring Data JPA |
| Build Tool | Gradle |
| API Documentation | Swagger / OpenAPI |
| DevOps | Docker, Docker Compose |
| Cloud | Railway |
| Tools | Git, GitHub, Postman, IntelliJ IDEA |

---

# 📁 Project Structure

```text
src
├── config
├── controller
├── dto
├── entity
├── exception
├── filter
├── repository
├── security
├── service
└── util
```

---

# 🔐 Authentication Flow

1. Register a new account
2. Login using email and password
3. Receive JWT Access Token
4. Click **Authorize** in Swagger
5. Enter

```
Bearer <your_token>
```

6. Access secured endpoints

---

# 🚀 Getting Started

## Clone Repository

```bash
git clone https://github.com/Abdullah-Ansar/task-management-system.git

cd task-management-system
```

---

## Configure Database

Update your datasource configuration.

```properties
spring.datasource.url=YOUR_DATABASE_URL
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

---

## Run Application

```bash
./gradlew bootRun
```

or

```bash
gradlew bootRun
```

Swagger

```
http://localhost:8080/swagger-ui/index.html
```

---

# 🐳 Docker

Build Image

```bash
docker build -t task-management-system .
```

Run Container

```bash
docker run -p 8080:8080 task-management-system
```

---

## Docker Compose

```bash
docker compose up --build
```

---

# 📚 API Documentation

### Swagger UI

https://task-management-system-production-cb49.up.railway.app/swagger-ui/index.html

### OpenAPI Docs

https://task-management-system-production-cb49.up.railway.app/v3/api-docs

---

# 📸 Screenshots

### Swagger UI

> Add screenshot here

### Login API

> Add screenshot here

### Register API

> Add screenshot here

### Task APIs

> Add screenshot here

### Docker Containers

> Add screenshot here

### Railway Deployment

> Add screenshot here

---

# 🚀 Future Enhancements

- Refresh Tokens
- Email Verification
- Password Reset
- Team Collaboration
- File Attachments
- Notifications
- Activity Logs
- Unit Testing
- Integration Testing
- CI/CD Pipeline
- Kubernetes
- AWS Deployment
- Microservices Architecture

---

# 👨‍💻 Author

**Abdullah Ansari**

📍 Gonda, Uttar Pradesh, India

**LinkedIn**

https://linkedin.com/in/abdullah-ansari-749a72178

**GitHub**

https://github.com/Abdullah-Ansar

**Email**

abdullah.ab68@gmail.com

---

## ⭐ If you like this project, please consider giving it a Star!

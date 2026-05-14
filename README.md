# Task Management and Team Collaboration System

A backend REST API project built using Java, Spring Boot, Gradle, MySQL, and Spring Security.

This project provides a complete task management backend system with authentication, JWT-based authorization, pagination, validation, filtering, and clean layered architecture following industry-level backend development practices.

---

## 🚀 Features

### 🔐 Authentication & Security
- User Registration
- User Login
- JWT Token Generation
- Password Encryption using BCrypt
- Spring Security Integration

### 📋 Task Management
- Create Task
- Get All Tasks
- Get Task By ID
- Update Task
- Delete Task

### ⚡ Advanced Backend Features
- Pagination
- Sorting
- Search APIs
- Filter Tasks by Status
- Validation Handling
- Global Exception Handling
- DTO Architecture
- Enum-based Status Management

---

## 🛠️ Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- JWT Authentication
- Spring Data JPA
- MySQL
- Gradle
- Hibernate
- Postman
- Git & GitHub

---

## 📂 Project Structure

```text
src/main/java/com/example/taskmanager
│
├── controller
├── service
├── repository
├── entity
├── dto
├── exception
├── config
├── security
```

---

## 🔥 Authentication APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login user |

---

## 📋 Task APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/tasks | Create task |
| GET | /api/tasks | Get all tasks |
| GET | /api/tasks/{id} | Get task by ID |
| PUT | /api/tasks/{id} | Update task |
| DELETE | /api/tasks/{id} | Delete task |

---

## 🔍 Filtering & Search APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/tasks/status/{status} | Filter tasks by status |
| GET | /api/tasks/search?keyword=spring | Search tasks by title |

---

## 📄 Pagination & Sorting Example

```http
GET /api/tasks?page=0&size=5&sortBy=title
```

---

## 🔐 JWT Login Example

### Request

```json
{
  "email": "abdullah@gmail.com",
  "password": "123456"
}
```

### Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

## 📌 Status Enum

```java
TODO
IN_PROGRESS
DONE
```

---

## 🧠 Concepts Implemented

- REST APIs
- Layered Architecture
- DTO Pattern
- Validation
- Exception Handling
- Pagination & Sorting
- Search & Filtering
- Spring Security
- JWT Authentication
- Password Encryption
- ResponseEntity
- Enum Usage

---

## ▶️ Run Project

### Clone Repository

```bash
git clone https://github.com/Abdullah-Ansar/task-management-system.git
```

---

### Configure Database

Create MySQL database:

```sql
CREATE DATABASE task_manager;
```

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/task_manager
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

---

### Start Application

```bash
./gradlew bootRun
```

---

## 👨‍💻 Author

Abdullah Ansari

GitHub:
https://github.com/Abdullah-Ansar

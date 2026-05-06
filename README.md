# Task Management and Team Collaboration System

A backend REST API project built using Java, Spring Boot, Gradle, and MySQL.

This project allows users to create, update, manage, and organize tasks efficiently using clean layered architecture and industry-level backend practices.

---

## 🚀 Features

- Create Task
- Get All Tasks
- Get Task By ID
- Update Task
- Delete Task
- Pagination Support
- Validation Handling
- Global Exception Handling
- DTO Architecture
- Enum-based Status Management

---

## 🛠️ Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- Gradle
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
```

---

## 🔥 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/tasks | Create task |
| GET | /api/tasks | Get all tasks |
| GET | /api/tasks/{id} | Get task by ID |
| PUT | /api/tasks/{id} | Update task |
| DELETE | /api/tasks/{id} | Delete task |

---

## 📄 Pagination Example

```http
GET /api/tasks?page=0&size=5
```

---

## ⚠️ Validation Example

```json
{
  "title": "Title is required"
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

- Layered Architecture
- DTO Pattern
- Validation
- Exception Handling
- Pagination
- REST APIs
- ResponseEntity
- Enum Usage

---

## ▶️ Run Project

### Clone Repository

```bash
git clone https://github.com/Abdullah-Ansar/task-management-system.git
```

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

## ▶️ Start Application

```bash
./gradlew bootRun
```

---

## 👨‍💻 Author

Abdullah Ansari

GitHub:
https://github.com/Abdullah-Ansar

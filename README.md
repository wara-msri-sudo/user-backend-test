# User Management API

A RESTful API for managing users, built with Spring Boot 3 and Java 21.

## Tech Stack

- **Java 21**
- **Spring Boot 3.5** (Web, Validation)
- **Lombok**
- **Maven**
- **Docker** (multi-stage build with Amazon Corretto 21)
- **GitHub Actions** (CI/CD)

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+

### Run Locally

```bash
./mvnw spring-boot:run
```

The server starts on `http://localhost:8080`.

### Run with Docker

```bash
docker build -t user-management:latest .
docker run -p 8080:8080 user-management:latest
```

## API Endpoints

Base path: `/users`

| Method | Path          | Description       | Status |
|--------|---------------|-------------------|--------|
| POST   | `/users`      | Create a user     | 201    |
| GET    | `/users`      | List all users    | 200    |
| GET    | `/users/{id}` | Get user by ID    | 200    |
| PUT    | `/users/{id}` | Update a user     | 200    |
| DELETE | `/users/{id}` | Delete a user     | 204    |

### Request Body (POST / PUT)

```json
{
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "phone": "123-456-7890",
  "website": "johndoe.com"
}
```

`name`, `username`, and `email` are required.

### Response Body

```json
{
  "id": 1,
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "phone": "123-456-7890",
  "website": "johndoe.com"
}
```

### Error Response

```json
{
  "message": "user does not exist",
  "timestamp": "2026-06-15T13:18:00.600126969"
}
```

## Storage

Users are stored in-memory using a `HashMap`. Data is not persisted across restarts.

## Testing

```bash
./mvnw test
```

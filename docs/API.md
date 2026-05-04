# Portfolio API Documentation

Base URL (local): `http://localhost:8080`

All JSON request and response bodies use `application/json`.

## Public endpoints

### GET /api/projects

Returns portfolio projects ordered by `displayOrder`, then `id`.

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "title": "Enterprise Employee & Payroll Management System",
    "description": "…",
    "techStack": "Java 17, Spring Boot, …",
    "displayOrder": 1
  }
]
```

### GET /api/skills

Returns skills ordered by `category`, then `displayOrder`.

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "name": "Java",
    "category": "BACKEND",
    "displayOrder": 1
  }
]
```

`category` is one of: `BACKEND`, `FRONTEND`, `DATABASE`, `TOOLS`.

### GET /api/experience

Returns work experience entries ordered by `displayOrder`.

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "roleTitle": "Full Stack Java Developer (Internship)",
    "organization": "Software development team",
    "summary": "…",
    "startPeriod": "2024",
    "endPeriod": "Present",
    "displayOrder": 1
  }
]
```

### POST /api/contact

Persists a contact form message.

**Request body:**

| Field   | Constraints                          |
|---------|--------------------------------------|
| name    | Required, max 120                    |
| email   | Required, valid email, max 254     |
| subject | Required, max 200                    |
| message | Required, max 4000                   |

```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "subject": "Collaboration",
  "message": "Hello Niral, …"
}
```

**Response:** `201 Created`

```json
{
  "id": 1,
  "name": "Jane Doe",
  "email": "jane@example.com",
  "subject": "Collaboration",
  "createdAt": "2026-05-01T12:00:00Z"
}
```

**Validation errors:** `400 Bad Request` with `ApiErrorResponse` shape:

```json
{
  "timestamp": "2026-05-01T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": ["email: Email must be valid"]
}
```

## Authentication (optional admin)

### POST /api/auth/login

Issues a JWT for the configured admin user (`admin.username` / `admin.password` in `application.yml`).

**Request:**

```json
{
  "username": "admin",
  "password": "changeme"
}
```

**Response:** `200 OK`

```json
{
  "token": "<jwt>",
  "type": "Bearer",
  "expiresInMs": 86400000
}
```

**Invalid credentials:** `401 Unauthorized`

### GET /api/admin/ping

Protected example endpoint. Requires header:

`Authorization: Bearer <jwt>`

**Response:** `200 OK`

```json
{
  "status": "ok",
  "role": "admin"
}
```

**Missing or invalid token:** `403 Forbidden` (Spring Security).

## CORS

Allowed origins are configured via `portfolio.cors.allowed-origins` (comma-separated), defaulting to the Angular dev server on port 4200.

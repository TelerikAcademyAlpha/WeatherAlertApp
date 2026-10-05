# Secure Weather Alert Service
## Technical Documentation

### 1. Project Overview

Secure Weather Alert Service is a Spring Boot application for managing
weather alerts. The application provides CRUD operations for weather alerts
and automatically enriches newly created alerts with geographic coordinates
based on the provided location name.

The application uses role-based authorization with two roles: ADMIN and USER.
ADMIN users can perform all CRUD operations, while USER accounts have
read-only access to weather alerts.

The project also includes a React frontend for authentication, alert
management, and administrative user management.

---

## 2. External Geolocation API

The application uses the Open-Meteo Geocoding API for geographic lookup.

When an alert is created, its location name is sent to the geocoding service.
If a matching location is found, the latitude and longitude are extracted
and stored in the corresponding weather alert.

Open-Meteo was selected because it provides a simple public geocoding API
and does not require API credentials for the functionality used by this
project.

If the external service cannot find the requested location, the latitude
and longitude remain null. External API failures are handled without
failing the original alert creation request, and the failure is logged.

---

## 3. Authentication and Authorization

Security is implemented using Spring Security.

The application uses session-based authentication. After successful login,
the server maintains the authenticated user's session using the standard
HTTP session mechanism.

CSRF protection is enabled. The frontend obtains a CSRF token from the
backend and includes it in state-changing requests.

Two application roles are supported:

- ADMIN
- USER

Authorization rules:

- ADMIN users can create, read, update and delete weather alerts.
- USER accounts have read-only access to weather alerts.
- Administrative user-management endpoints are accessible only to ADMIN
  users.

Passwords are never stored as plain text and are encoded using BCrypt.

The frontend also hides administrative functionality from USER accounts,
while the backend remains the authoritative security layer and enforces
authorization independently of the frontend.

---

## 4. Asynchronous Geolocation Enrichment

Geolocation enrichment is performed asynchronously so that communication
with the external API does not delay the alert creation endpoint.

When a new weather alert is created:

1. The alert is first persisted in PostgreSQL without coordinates.
2. A geocoding request event is published.
3. The POST request can return the initial alert immediately.
4. After the database transaction commits, an asynchronous listener
   processes the geocoding request.
5. The external Open-Meteo API is queried using the alert's location name.
6. If coordinates are found, the alert is updated with latitude and
   longitude.

Spring's asynchronous execution support is used with a dedicated task
executor.

Processing the event after the original transaction commits ensures that
the asynchronous worker operates on an alert that has already been
successfully persisted.

When an alert's location name is changed, its previous coordinates are
cleared and a new geocoding enrichment request is triggered.

---

## 5. Persistence and Architecture

PostgreSQL is used as the relational database.

Spring Data JPA is used for persistence and Flyway is used for database
schema migrations.

The backend is separated into responsibilities such as:

- controllers - HTTP API layer
- services - application and business logic
- repositories - persistence access
- models / DTOs - entities and API data structures
- security - authentication and authorization
- configuration - Spring configuration
- events - asynchronous geocoding communication
- exception handling - consistent API error handling

DTOs are used instead of exposing persistence entities directly through
the REST API.

---

## 6. Testing

Unit tests are implemented using JUnit 5 and Mockito.

The service tests cover important scenarios including:

- alert creation
- retrieval of alerts
- alert updates
- alert deletion
- geocoding event publication
- coordinate reset when the location changes
- user registration
- duplicate usernames
- password encoding
- blocking and unblocking users

Security and API behavior can additionally be verified using integration
tests for role-based access to protected endpoints.

---

## 7. Architectural Decisions and Trade-offs

Session-based authentication was selected instead of JWT because the
application contains a browser-based frontend and does not require
stateless authentication across multiple independent clients.

Asynchronous event-driven geolocation enrichment keeps the alert creation
endpoint responsive and separates persistence of the alert from the
external API call.

The external API request itself may use blocking I/O, but it is executed
on the dedicated asynchronous executor rather than on the HTTP request
thread. This provides a simple implementation appropriate for the scope
of the project.

PostgreSQL provides persistent relational storage, while Flyway makes
database schema changes reproducible and version controlled.
# Secure Weather Alert Service

A Spring Boot and React application for managing weather alerts with
asynchronous geographic coordinate enrichment.

## Features

- Weather alert CRUD API
- PostgreSQL persistence
- ADMIN and USER roles
- Session-based authentication
- CSRF protection
- Asynchronous geolocation enrichment
- Open-Meteo Geocoding API integration
- User administration with block/unblock functionality
- React frontend
- Swagger / OpenAPI documentation
- Unit tests
- Flyway database migrations

## Technology Stack

### Backend

- Java
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- WebClient
- Spring Async
- JUnit 5
- Mockito
- Springdoc OpenAPI

### Frontend

- React
- Vite
- React Router
- Axios

## Requirements

Before running the project, install:

- Java 21
- Maven
- PostgreSQL
- Node.js
- npm

## Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE weather_alert_db;
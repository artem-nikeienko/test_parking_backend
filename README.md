# Smart Parking System Backend

![Java 21+]
![Spring Boot]
![Gradle]

A backend system for managing smart parking infrastructure: parking lots, levels, slots, and vehicle parking sessions with automated fee calculation.

The project focuses on **clean architecture, domain modeling, and extensible REST API design**.

---

## Features

- Parking lot management (create / delete)
- Level and slot management
- Vehicle check-in / check-out flow
- Automatic parking session tracking
- Fee calculation based on vehicle type and duration
- RESTful API with clean resource modeling
- In-memory persistence (simplified repository layer)
- Main cases test coverage (unit + integration)

---

## Architecture Overview

The system follows a layered, domain-driven architecture:

- **Domain Layer** – business rules and invariants (Lot, Level, Slot, Session)
- **Service Layer** – use case orchestration (check-in, check-out, allocation)
- **Controller Layer** – REST API endpoints
- **Repository Layer** – in-memory storage abstraction

Key idea: business logic lives in the domain, not in controllers.

---

## Domain Model Highlights

- **Lot** – aggregate root (UUID-based identity)
- **Level** – identified by number within a lot
- **Slot** – incremental ID within level
- **Vehicle** – type influences slot compatibility and pricing
- **Parking Session** – represents active parking state

### Design decisions

- Slot deletion is supported but assumed rare (future: soft delete)
- DTOs currently reused for simplicity (future: separation of API vs domain models)
- Strategy pattern intended for fee calculation extensibility

---

## API Overview

Base URL:

```
/api/v1
```

Main resources:

- `/parking/lots`
- `/parking/lots/{lotId}/levels`
- `/parking/lots/{lotId}/levels/{levelNumber}/slots`
- `/sessions`

---

Currently, the API operates at Level 2 of the Richardson Maturity Model (resource-based with HTTP verbs).

---

## Quickstart


### 0. Run auto-tests

```bash
./gradlew test
```

### 1. Build the project

```bash
./gradlew clean build
```

### 2. Run the application

```bash
./gradlew bootRun
```

Application starts at:

```
http://localhost:8080
```

---

## API Calls Collection

```
Download https://raw.githubusercontent.com/artem-nikeienko/test_parking_backend/refs/heads/dev/Insomnia_requests_collection.yaml as a requests collection (may be imported to Insomnia or Postman)
```

## API Examples (curl)

### Create Parking Lot

```bash
curl -X POST http://localhost:8080/api/v1/parking/lots \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Central Parking"
  }'
```

---

### Add Level to Lot

```bash
curl -X POST http://localhost:8080/api/v1/parking/lots/{lotId}/levels \
  -H "Content-Type: application/json" \
  -d '{
    "number": 1
  }'
```

---

### Add Slot

```bash
curl -X POST http://localhost:8080/api/v1/parking/lots/{lotId}/levels/1/slots \
  -H "Content-Type: application/json" \
  -d '{
    "type": "COMPACT"
  }'
```

---

### Vehicle Check-in

```bash
curl -X POST http://localhost:8080/api/v1/sessions/lots/{lotId} \
  -H "Content-Type: application/json" \
  -d '{
    "vehicle": {
      "licensePlate": "ABC-123",
      "type": "CAR"
    }
  }'
```

---

### Vehicle Check-out

```bash
curl -X POST http://localhost:8080/api/v1/sessions/{sessionId}/check-out
```

---

### Get Active Sessions

```bash
curl -X GET http://localhost:8080/api/v1/sessions/lots/{lotId}
```

---

## Key Assumptions & Limitations

- No authentication/authorization layer
- In-memory storage only (no persistence)
- No batch creation of nested entities
- Basic concurrency handling (not production-grade)
- DTOs shared between domain and API (temporary simplification)
- Currency not included in fee response
- Slot deletion is physical (future improvement: soft delete)

---

## Future Improvements

- Introduce HATEOAS (Hypermedia as the Engine of Application State) to reach Richardson Maturity Model Level 3 and enable discoverable API navigation to guide client interactions and reduce coupling
- Introduce persistence layer (JPA / PostgreSQL)
- Separate API DTOs from domain models
- Add authentication & role-based access
- Improve concurrency control (locking / optimistic versioning)
- Extend fee calculation using Strategy Pattern
- Add batch creation for levels and slots

---

## Design Philosophy

This project prioritizes:

- clear domain modeling over infrastructure complexity
- RESTful, resource-oriented API design
- extensibility over premature optimization
- simplicity in persistence and runtime setup

---

## License

Educational / assessment project.


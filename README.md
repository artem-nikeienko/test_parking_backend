# Smart Parking System

## Overview
Backend service for managing parking lots, vehicle check-in/check-out, and fee calculation.  
Built with focus on clean architecture, extensibility, and testability.

---

## Features
- Parking lot / level / slot management
- Vehicle check-in & check-out
- Active session tracking
- Fee calculation (strategy-based)

---

## Domain Model
- Lot: UUID, unique name, contains levels  
- Level: number (unique per lot), contains slots  
- Slot: type + status (AVAILABLE / OCCUPIED / UNAVAILABLE)  
- Vehicle: licensePlate (unique among ACTIVE sessions), type  
- Session: lifecycle of parking (ACTIVE / COMPLETED)

Assumptions:
- UUID for lots (no collision risk)
- Level = (lotId + number)
- Slot = derived/composite ID
- One ACTIVE session per vehicle
- Currency omitted for simplicity

---

## Architecture
DDD-inspired layered design:
- domain (model, policy, strategy)
- service
- repository (in-memory)
- controller (REST)

Key Decisions:
- Strategy Pattern for pricing
- Partial rich domain model
- In-memory storage (per assignment constraints)

---

## Business Flows

Check-in:
1. Validate if vehicle is not registered already  
2. Find compatible available slot  
3. Allocate slot  
4. Create session  

Check-out:
1. Load session  
2. Validate state  
3. Calculate fee  
4. Complete session  
5. Free slot  

---

## Fee Calculation
Based on duration + vehicle type:
- Motorcycle: $1/h  
- Car: $2/h  
- Truck: $3/h  

Extensible via implementing of FeeStrategy.

---

## API

Base URL: https://localhost:8080

Lot Management:
POST   /parking/lots  
POST   /parking/lots/{lotId}/levels  
POST   /parking/lots/{lotId}/levels/{levelNumber}/slots  
PATCH  /parking/lots/{lotId}/levels/{levelNumber}/slots/{slotId}  
DELETE /parking/lots/{lotId}  

Sessions:
POST /sessions/lots/{lotId}  
POST /sessions/{sessionId}/check-out  
GET  /sessions/lots/{lotId}  

Example Check-in:
{
  "vehicle": {
    "licensePlate": "AA1234",
    "type": "CAR"
  }
}

Download https://github.com/artem-nikeienko/test_parking_backend/blob/dev/Insomnia_2026-04-30.yaml as a requests collection (may be imported to Insomnia or Postman)

---

## Error Format
{
  "code": "ERROR_CODE",
  "message": "Human readable message"
}

Assumptions:
- Generic reusable errors
- No auth (401/403 omitted)

---

## Testing
- Unit + service tests
- In-memory repositories (minimal mocking)
- Covers:
  - happy path
  - business rules
  - edge cases

---

## Design Notes & TODOs
- Add HATEOAS to REST controllers
- Improve slot allocation strategy
- Add concurrency control (locking / optimistic)
- Add validation layer
- Introduce transactions
- Add persistence (DB)
- Add auth / roles
- Add pagination & filtering
- Add audit/history

---

## Limitations
- No database
- Minimum concurrency safety
- No authentication
- Limited querying

---

## Run

Requirements:
- Java 21+
- Gradle

Start:
./gradlew bootRun

Tests:
./gradlew test

---

## Structure
controller/
service/
domain/
repository/

---

## Notes
System is simplified but designed for easy evolution to production-ready architecture.
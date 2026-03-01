# Restaurant POS System

A microservice-based REST API for a restaurant point-of-sale system, built with Spring Boot, MongoDB, and RabbitMQ.

---

## Architecture

The system is composed of two independent services structured using the **package-by-feature** pattern:

- **Menu Service** — manages the restaurant's menu catalog. Menu items created here are consumed by the Order Service.
- **Order Service** — handles order creation, status updates, and customer notifications via RabbitMQ.

```
restaurant-pos-system/
├── menu-service/       → manages menu items (port 8081)
└── order-service/      → manages orders + RabbitMQ integration (port 8080)
```

### Communication
- **Synchronous**: Order Service fetches menu item details from Menu Service via REST (RestTemplate)
- **Asynchronous**: Order Service publishes a message to RabbitMQ when an order status changes, then consumes it to simulate customer notification by logging `fullName`, `address`, `email`, and the new `status`

### Infrastructure
All services run inside Docker containers:

| Container | Description |
|---|---|
| `menu-service` | Menu management API (port 8081) |
| `order-service` | Order management API + RabbitMQ integration (port 8080) |
| `mongodb` | Shared MongoDB instance |
| `rabbitmq` | Message broker (management UI on port 15672) |

---

## Order Status

Beyond the `CREATED` and `DELIVERED` statuses described in the spec, two additional statuses were implemented:

| Status | Description |
|---|---|
| `CREATED` | Order has been placed |
| `PREPARING` | Order is being prepared |
| `READY` | Order is ready for delivery |
| `DELIVERED` | Order has been delivered |

---

## How to Run

**Requirements:** Docker and Docker Compose installed.

```bash
# Clone the repository
git clone https://github.com/Macedopy/backend-challenge
cd backend-challenge/restaurant-pos-system

# Start all services
docker compose up -d --build
```

This will spin up all four containers: `menu-service`, `order-service`, `mongodb`, and `rabbitmq`.

### Service URLs

| Service | URL |
|---|---|
| Menu Service | http://localhost:8081 |
| Order Service | http://localhost:8080 |
| RabbitMQ Management | http://localhost:15672 (guest/guest) |

---

## API Examples

You can import the provided Insomnia collection file `backend-challenge-request-example` directly into [Insomnia](https://insomnia.rest) to get all requests ready to use.

### Menu Service

**Create Menu Item**
```bash
curl -X POST http://localhost:8081/menu-items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pizza",
    "description": "Delicious cheese pizza",
    "price": 9.99
  }'
```

**Update Menu Item**
```bash
curl -X PUT http://localhost:8081/menu-items/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Large Pizza",
    "description": "Cheese pizza with extra toppings",
    "price": 11.99
  }'
```

**Delete Menu Item**
```bash
curl -X DELETE http://localhost:8081/menu-items/{id}
```

**Get All Menu Items (Paginated)**
```bash
curl "http://localhost:8081/menu-items?limit=10&offset=0"
```

**Get Menu Item by ID**
```bash
curl http://localhost:8081/menu-items/{id}
```

---

### Order Service

**Create Order**
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customer": {
      "fullName": "John Doe",
      "address": "123 Main St",
      "email": "john@example.com"
    },
    "orderItems": [
      { "productId": "abc123", "quantity": 2 },
      { "productId": "xyz456", "quantity": 1 }
    ]
  }'
```

**Update Order Status**
```bash
curl -X PATCH http://localhost:8080/orders/{id}/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "PREPARING"
  }'
```

**Get Order History (Paginated)**
```bash
curl "http://localhost:8080/orders?limit=10&offset=0"
```

**Get Order by ID**
```bash
curl http://localhost:8080/orders/{id}
```

---

## Tech Stack

| Technology | Version |
|---|---|
| Java | 21 |
| Groovy | 5.0.4 |
| Spring Boot | 3.4.3 |
| Spring Dependency Management | 1.1.7 |
| Spring Data MongoDB | managed by Spring Boot 3.4.3 |
| Spring AMQP (RabbitMQ) | managed by Spring Boot 3.4.3 |
| MongoDB | latest |
| RabbitMQ | latest |
| Spock Framework | 2.4-groovy-5.0 |
| JaCoCo | 0.8.12 |
| Docker / Docker Compose | — |

---

## Tests

Unit tests are implemented with **Spock Framework** covering:

- Service layer (business logic, exception handling)
- Controller layer (HTTP status codes, request/response)
- Exception handlers (400, 404, 409, 500)

```bash
# Run tests — Menu Service
cd menu-service && ./gradlew test

# Run tests with coverage report — Menu Service
cd menu-service && ./gradlew test jacocoTestReport

# Run tests — Order Service
cd order-service && ./gradlew test

# Run tests with coverage report — Order Service
cd order-service && ./gradlew test jacocoTestReport
```

Coverage reports are available at `build/reports/jacoco/test/html/index.html`.

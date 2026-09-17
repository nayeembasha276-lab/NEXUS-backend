# NEXUS — Adaptive Sorting Intelligence Engine
NEXUS is a secure Spring Boot backend that intelligently executes and benchmarks sorting algorithms based on user-provided datasets.
The system provides authenticated users with sorting execution, algorithm benchmarking, execution history, dashboard analytics, pagination, and user-isolated data access.

## Live Deployment
### Backend
https://nexus-backend-urze.onrender.com
### Frontend
https://nexus-frontend-steel-eight.vercel.app
### Backend Repository
https://github.com/nayeembasha276-lab/NEXUS-backend
### Database
Aiven MySQL

##  Architecture

                    ┌──────────────────────┐
                    │        User          │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │   Vercel Frontend    │
                    │   HTML CSS JavaScript │
                    └──────────┬───────────┘
                               │ HTTPS / REST API
                               ▼
                    ┌──────────────────────┐
                    │ Render Docker Server │
                    │   Spring Boot API    │
                    └──────────┬───────────┘
                               │
                     ┌─────────┴─────────┐
                     ▼                   ▼
             ┌──────────────┐    ┌──────────────┐
             │ JWT Security │    │  NEXUS Engine│
             └──────────────┘    └──────┬───────┘
                                        │
                                        ▼
                               ┌────────────────┐
                               │  Aiven MySQL   │
                               │ users/history  │
                               └────────────────┘



##  Key Features

* User registration and login
* JWT-based authentication
* BCrypt password hashing
* Role-based authorization
* Sorting algorithm execution
* Intelligent algorithm selection
* Algorithm benchmarking
* Execution history
* Dashboard analytics
* Pagination for execution history
* User-isolated data access
* Individual history deletion
* Complete history deletion
* Input validation
* Global exception handling
* CORS configuration
* Dockerized deployment
* Environment-based configuration
* Aiven MySQL cloud database
* Render backend deployment
* Vercel frontend integration
* Maximum dataset protection
* RESTful API architecture

##  NEXUS Intelligence Engine

NEXUS is designed around a decision-engine approach.
Instead of simply executing one fixed sorting algorithm, the backend can analyze the input and select an appropriate sorting strategy.
Supported sorting algorithms include the algorithms implemented in the project such as:
* Insertion Sort
* Merge Sort
* Quick Sort
* Heap Sort

The exact algorithm selection depends on the NEXUS decision engine and input characteristics.

##  Technology Stack
### Backend

* Java 21
* Spring Boot 3.3.5
* Spring Web
* Spring Data JPA
* Spring Security
* Hibernate
* Jakarta Validation
* JWT
* Lombok

### Database

* MySQL
* Aiven MySQL

### DevOps

* Docker
* Render
* GitHub
* Environment Variables

### API Testing

* Postman

### Frontend

* HTML
* CSS
* JavaScript
* Vercel

#  Security

NEXUS uses JWT authentication.

Authentication flow:
Register
   ↓
Login
   ↓
JWT Token
   ↓
Authorization Header
   ↓
Protected API

Protected requests use:

Authorization: Bearer <JWT_TOKEN>

Passwords are stored using BCrypt hashing.

User-specific resources are protected so one authenticated user cannot access another user's execution history.

#  REST API Documentation

Base URL:https://nexus-backend-urze.onrender.com/api

## 1. Register User
### POST
/api/auth/register
### Full URL
https://nexus-backend-urze.onrender.com/api/auth/register
### Body

 json
{
  "name": "Nayeem Basha",
  "email": "nayeem@example.com",
  "username": "nayeem",
  "password": "Password@123"
}
### Expected Response
201 Created

# 2. Login
### POST
/api/auth/login
### Full URL
https://nexus-backend-urze.onrender.com/api/auth/login
### Body

json
{
  "username": "nayeem",
  "password": "Password@123"
}
### Expected Response

json
{
  "token": "JWT_TOKEN"
}
Copy the JWT token.
For protected APIs use: http Authorization: Bearer JWT_TOKEN


# 3. Logout
### POST
/api/auth/logout
### Full URL
https://nexus-backend-urze.onrender.com/api/auth/logout
### Header
Authorization: Bearer JWT_TOKEN
NEXUS currently follows a stateless JWT approach. Logout is handled on the client by removing the stored token.

# 4. Execute Sorting
### POST
/api/execute
### Full URL
https://nexus-backend-urze.onrender.com/api/execute
### Header
Authorization: Bearer JWT_TOKEN
Content-Type: application/json
### Example Body
json
{
  "data": [64, 25, 12, 22, 11],
  "algorithm": "QUICK_SORT"
}
Use the algorithm names supported by the backend implementation.
The execution stores the result and execution information in the database.

# 5. Benchmark Algorithms
### POST
/api/benchmark
### Full URL
https://nexus-backend-urze.onrender.com/api/benchmark
### Header
Authorization: Bearer JWT_TOKEN
Content-Type: application/json
### Example Body
json
{
  "data": [64, 25, 12, 22, 11]
}
The benchmark endpoint compares the supported sorting algorithms and records execution performance.

# 6. Dashboard
### GET
/api/dashboard
### Full URL
https://nexus-backend-urze.onrender.com/api/dashboard
### Header
Authorization: Bearer JWT_TOKEN
Returns dashboard-related execution statistics for the authenticated user.

# 7. Execution History
### GET
/api/history
### Full URL
https://nexus-backend-urze.onrender.com/api/history
### Header
Authorization: Bearer JWT_TOKEN

### Pagination
/api/history?page=0&size=10
Example: https://nexus-backend-urze.onrender.com/api/history?page=0&size=10
Pagination prevents the backend from loading the complete history dataset at once.

# 8. Delete One History Record
### DELETE
/api/history/{id}
### Example
/api/history/1
### Header
Authorization: Bearer JWT_TOKEN
Deletes the specified execution history record belonging to the authenticated user.

# 9. Delete All History
### DELETE
/api/history
### Full URL
https://nexus-backend-urze.onrender.com/api/history
### Header
Authorization: Bearer JWT_TOKEN
Deletes execution history belonging to the authenticated user.

# Postman Testing Flow
Recommended testing order:
1. Register
      ↓
2. Login
      ↓
3. Copy JWT Token
      ↓
4. Execute Sorting
      ↓
5. Benchmark
      ↓
6. Dashboard
      ↓
7. History
      ↓
8. Delete One History
      ↓
9. History Again
      ↓
10. Delete All History
      ↓
11. History Again
      ↓
12. Logout


#  Postman Environment

Create a Postman environment:
NEXUS_API_URL
NEXUS_TOKEN
Set:NEXUS_API_URL = https://nexus-backend-urze.onrender.com/api
After login, store the JWT token as: NEXUS_TOKEN
Then protected requests can use:
Authorization: Bearer {{NEXUS_TOKEN}}

#  Postman Test Cases
## Test 1 — Registration
POST {{NEXUS_API_URL}}/auth/register
Body:
json
{
  "name": "Test User",
  "email": "testuser@nexus.com",
  "username": "testuser",
  "password": "Test@12345"
}
Expected:
201 Created

## Test 2 — Login
POST {{NEXUS_API_URL}}/auth/login
Body:
json
{
  "username": "testuser",
  "password": "Test@12345"
}
Expected:
text
200 OK
Copy token into: NEXUS_TOKEN

## Test 3 — Execute
POST {{NEXUS_API_URL}}/execute
Authorization: Bearer {{NEXUS_TOKEN}}
Body:
json
{
  "data": [50, 10, 40, 20, 30],
  "algorithm": "QUICK_SORT"
}
Expected : 200 OK

## Test 4 — Benchmark
POST {{NEXUS_API_URL}}/benchmark
Authorization: Bearer {{NEXUS_TOKEN}}
Body:
json
{
  "data": [50, 10, 40, 20, 30]
}
Expected:200 OK

## Test 5 — Dashboard
GET {{NEXUS_API_URL}}/dashboard
Authorization: Bearer {{NEXUS_TOKEN}}
Expected: 200 OK

## Test 6 — History
GET {{NEXUS_API_URL}}/history?page=0&size=10
Authorization: Bearer {{NEXUS_TOKEN}}
Expected:200 OK

## Test 7 — Delete History
DELETE {{NEXUS_API_URL}}/history/1
Authorization: Bearer {{NEXUS_TOKEN}}
Expected: 200 OK
Use an actual history ID returned by the history API.

## Test 8 — Delete All History
DELETE {{NEXUS_API_URL}}/history
Authorization: Bearer {{NEXUS_TOKEN}}
Expected: 200 OK

## Test 9 — Logout
POST {{NEXUS_API_URL}}/auth/logout
Authorization: Bearer {{NEXUS_TOKEN}}
Expected:200 OK

#  Error Handling
NEXUS provides structured error handling for common API failures.
Examples:

| Error                    | HTTP Status |
| ------------------------ | ----------: |
| Invalid request          |         400 |
| Validation failure       |         400 |
| Invalid credentials      |         401 |
| Missing/invalid JWT      |         401 |
| Access denied            |         403 |
| Resource not found       |         404 |
| Duplicate email/username |         409 |
| Database/server error    |         500 |


#  Data Protection
The application protects user-specific execution history.
Example:
User A
  ├── Execution 1
  ├── Execution 2
  └── Execution 3

User B
  ├── Execution 4
  └── Execution 5
User A cannot access User B's history.

#  Docker
NEXUS backend is containerized using Docker.
Build:
bash
docker build -t nexus-backend .
Run:
bash
docker run -p 8080:8080 nexus-backend
Production deployment uses the Dockerfile through Render.

#  Deployment
Production architecture:
GitHub
   ↓
Render Auto Deploy
   ↓
Docker Build
   ↓
Spring Boot Application
   ↓
Aiven MySQL


Frontend:
GitHub
   ↓
Vercel
   ↓
NEXUS Frontend

#  Environment Variables
Sensitive configuration is not hardcoded.
Required environment variables:
DB_URL
DB_USERNAME
MYSQL_PASSWORD
JWT_SECRET
JWT_EXPIRATION

Example structure:
DB_URL=jdbc:mysql://HOST:PORT/defaultdb?sslMode=REQUIRED
DB_USERNAME=avnadmin
MYSQL_PASSWORD=********
JWT_SECRET=********
JWT_EXPIRATION=86400000
Never commit real database passwords or JWT secrets to GitHub.

#  Database
Production database:
Aiven MySQL
Main data includes: users execution_history
Hibernate automatically manages the schema using:
propertie
spring.jpa.hibernate.ddl-auto=update


#  CORS
The backend allows requests from the production Vercel frontend and configured local development origins.
Production frontend: https://nexus-frontend-steel-eight.vercel.app


# Scalability Considerations
NEXUS includes protections for large datasets.
Current execution input is limited to: 1,00,000 values
Execution history uses pagination: ?page=0&size=10
This prevents unnecessarily loading large history datasets into memory.


# Local Development
Clone the repository:
bash
git clone https://github.com/nayeembasha276-lab/NEXUS-backend.git
Move into the project:
bash
cd NEXUS-backend
Run:bash
mvn spring-boot:run
Application: http://localhost:8080
API base: http://localhost:8080/api


#  Project Objective
The primary objective of NEXUS is to combine:
Data Structures
      +
Sorting Algorithms
      +
Algorithm Analysis
      +
Benchmarking
      +
Spring Boot
      +
JWT Security
      +
MySQL
      +
Docker
      +
Cloud Deployment into a practical backend engineering project.

# Author
**Nayeem Basha**

Java Backend Developer | BCA Student

GitHub:https://github.com/nayeembasha276-lab

LinkedIn:https://www.linkedin.com/in/nayeem-basha-69b607379

#  Future Enhancements
Planned improvements include:

* Redis caching
* Rate limiting
* Refresh tokens
* Email verification
* Forgot/reset password
* API versioning
* Advanced history filtering
* Structured logging
* Correlation IDs
* Spring Boot Actuator
* Flyway/Liquibase migrations
* Advanced monitoring
* CI/CD improvements
* Microservices evolution
##  NEXUS

**Adaptive Sorting Intelligence Engine**

Built with Java, Spring Boot, JWT, MySQL, Docker and cloud deployment.

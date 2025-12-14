# Expression Calculator (Spring Boot + MongoDB)

A simple arithmetic expression evaluator built with Java 21, Spring Boot 4, and MongoDB 8.0.  
Supports +, -,'*', /, parentheses, operator precedence, and stores results in MongoDB with caching (prevents duplicates).

---

## How to Run (macOS and Windows)

### macOS (Homebrew)
Install MongoDB:
brew tap mongodb/brew install mongodb-community@8.0

Start MongoDB:
brew services start mongodb-community@8.0

MongoDB runs on:
mongodb://localhost:27017

Run the Spring Boot app:
mvn clean package
mvn spring-boot:run

Or run the built jar:
java -jar target/ExpressionCalculator-0.0.1-SNAPSHOT.jar


### Windows

#### Option A — Install MongoDB via MSI
1. Download MongoDB Community Server MSI.
2. Install with “Run as service”.

Start the service:
Start-Service MongoDB

#### Option B — Install via Chocolatey
    choco install mongodb
    net start MongoDB

Run the Spring Boot app:
mvn clean package
mvn spring-boot:run


---

## Configuration

In `src/main/resources/application.properties`:

    spring.data.mongodb.uri=mongodb://localhost:27017/calculator-db
    server.port=8080

To change port:
macOS/Linux:
export SERVER_PORT=9090
Windows PowerShell:
$env:SERVER_PORT=9090


---

## API Documentation

Base URL:
http://localhost:8080/api/calculator


### POST /calculate
Evaluates an expression and stores or returns cached value.

Body JSON:
{
"expression": "2 + 3 * 4"
}

Response:
{
"expression": "2 + 3 * 4",
"result": 14.0,
"cached": false
}

If cached result exists, `cached` will be true.


### GET /search?result=VALUE
Example:
http://localhost:8080/api/calculator/search?result=14

Response:
[
{
"id": "xxxx",
"expression": "2 + 3 * 4",
"result": 14.0,
"createdAt": "2025-12-14T18:10:32"
}
]


---

## cURL Examples

Calculate:
curl -X POST http://localhost:8080/api/calculator/calculate \
-H "Content-Type: application/json" \
-d '{"expression":"(10 + 2) * 5"}'

Search:
curl "http://localhost:8080/api/calculator/search?result=60"


---

## Project Structure

src/main/java/...  
controller/  
service/  
repository/

src/main/resources  
application.properties


---

## Build Commands

    mvn clean package
    mvn spring-boot:run


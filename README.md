# Bank System Backend

This is the backend application for the Bank System system.

## Requirements
- Java 21
- Maven
- Postgres database

## How to Run

1. **Configure Database**: Ensure your `application.properties` has the correct database credentials.
2. **Build**:
   ```bash
   mvn clean package
   ```
3. **Run**:
   ```bash
   # Option 1: Using Maven
   mvn spring-boot:run

   # Option 2: Using the built JAR
   java -jar target/bank-0.0.1-SNAPSHOT.jar
   ```

## API Documentation (Swagger UI)

Once the application is running, you can access the interactive API documentation at:

**[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

This interface allows you to explore the available API endpoints and test them directly.

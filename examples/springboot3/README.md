# SmartMask Spring Boot 3 Example

This example project demonstrates how to use the SmartMask Spring Boot Starter library in a Spring Boot 3 application.

## Overview

This example shows:

1. How to integrate the SmartMask library into a Spring Boot 3 project
2. How to use the `@Sensitive` annotation to mask sensitive data
3. How masking works in both JSON responses and logs
4. How to use role-based access control for sensitive data

## Project Structure

- `SmartMaskDemoApplication.java`: Main application class with security configuration
- `User.java`: Model class with various examples of `@Sensitive` annotations
- `UserController.java`: REST controller with public and admin-only endpoints
- `UserService.java`: Service class demonstrating masking in logs

## Running the Example

### Prerequisites

- Java 17 or higher
- Maven

### Build and Run

```bash
mvn clean package
java -jar target/smartmask-demo-0.0.1-SNAPSHOT.jar
```

Or using Maven directly:

```bash
mvn spring-boot:run
```

## Testing the Example

### Testing API Responses

1. **Public endpoint (no authentication required)**:

```bash
curl http://localhost:8080/api/public/user
```

This will return a JSON response with all sensitive fields masked according to their masking rules.

2. **Admin endpoint (requires ADMIN role)**:

```bash
curl -u admin:admin http://localhost:8080/api/admin/user
```

This will return a JSON response where the `adminOnlyData` field is unmasked (because the admin user has the ROLE_ADMIN role).

3. **Creating a user**:

```bash
curl -X POST -H "Content-Type: application/json" -d '{
  "username": "newuser",
  "password": "password123",
  "email": "new.user@example.com",
  "creditCardNumber": "4111111111111111",
  "phoneNumber": "12345678901",
  "iban": "DE89370400440532013000",
  "partiallyVisibleData": "SENSITIVE_DATA_123",
  "lastFourVisible": "SENSITIVE_DATA_456",
  "customMaskChar": "CUSTOM_MASK_CHAR_DATA",
  "adminOnlyData": "This data is only visible to admins"
}' http://localhost:8080/api/public/user
```

### Observing Masking in Logs

When you run the application and make requests to the endpoints, you'll see log messages in the console. The sensitive fields in the User objects will be automatically masked in these logs.

For example, when you create a user, you'll see log messages like:

```
Creating new user: User{id=null, username='newuser', password='******', email='n******r@example.com', creditCardNumber='************1111', ...}
User created successfully: User{id=1, username='newuser', password='******', email='n******r@example.com', creditCardNumber='************1111', ...}
```

## Masking Types Demonstrated

This example demonstrates all available masking types:

- `MaskType.GENERIC`: Default masking (password field)
- `MaskType.EMAIL`: Email masking (email field)
- `MaskType.CREDIT_CARD`: Credit card masking (creditCardNumber field)
- `MaskType.PHONE_NUMBER`: Phone number masking (phoneNumber field)
- `MaskType.IBAN`: IBAN masking (iban field)

It also demonstrates customization options:

- Showing first N characters (partiallyVisibleData field)
- Showing last N characters (lastFourVisible field)
- Custom mask character (customMaskChar field)
- Role-based access control (adminOnlyData field)

## Logging Configuration

This example demonstrates how to use SmartMask's logging feature to automatically mask sensitive data in log messages. The logging configuration is set up in `src/main/resources/logback.xml`.

### How the Logging Feature Works

1. **MaskingMessageConverter**: The SmartMask library provides a Logback converter that intercepts log messages and masks sensitive data before it appears in logs.

2. **Configuration**: The converter is configured in `logback.xml` with the following key elements:
   ```xml
   <conversionRule conversionWord="maskedMsg" 
                  converterClass="io.github.christianklisch.smartmask.logging.MaskingMessageConverter" />
   ```

3. **Usage in Log Pattern**: The converter is used in the log pattern with the `%maskedMsg` conversion word:
   ```xml
   <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %maskedMsg%n</pattern>
   ```

4. **Automatic Masking**: When you log objects that contain fields annotated with `@Sensitive`, the converter automatically masks those fields according to their masking rules.

### Log Output Examples

When you run the application and make requests to the endpoints, you'll see log messages with masked sensitive data:

```
12:34:56.789 [http-nio-8080-exec-1] INFO  c.e.s.demo.service.UserService - Creating new user: User{id=null, username='newuser', password='******', email='n******r@example.com', creditCardNumber='************1111', ...}
12:34:56.890 [http-nio-8080-exec-1] INFO  c.e.s.demo.service.UserService - User created successfully: User{id=1, username='newuser', password='******', email='n******r@example.com', creditCardNumber='************1111', ...}
```

### Log Files

The application is configured to write logs to both the console and a file:
- Console: All logs are displayed in the terminal
- File: Logs are written to `logs/smartmask-demo.log` with automatic rotation

# SmartMask Spring Boot Starter

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Maven Central](https://img.shields.io/maven-central/v/io.github.christianklisch/smartmask-spring-boot-starter)
![Build Status](https://github.com/christianklisch/smartmask-spring-boot-starter/actions/workflows/ci.yml/badge.svg)


A unified data masking library for Spring Boot applications that delivers consistent masking of sensitive data across JSON responses and logs, designed to support GDPR/DSGVO compliance, audit logging, and security-critical logging scenarios.

> **If you find this project useful, please consider giving it a ⭐ on GitHub!**

## Features

- **Annotation-based**: Simply mark fields with `@Sensitive` to enable masking
- **Multiple masking strategies**: Specialized masking for emails, credit cards, phone numbers, IBANs, and generic data
- **Customizable masking**: Configure how many characters to show, which character to use for masking
- **Role-based access control**: Show unmasked data to authorized users based on Spring Security roles
- **Automatic integration**: Works with Spring Boot's auto-configuration
- **Dual protection**: Masks sensitive data in both JSON responses and logs

## Requirements

- Java 17 or higher
- Spring Boot 3.5.x
- Jackson (for JSON masking)
- Logback (for log masking)

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.christianklisch</groupId>
    <artifactId>smartmask-spring-boot-starter</artifactId>
    <version>0.3.0</version>
</dependency>
```

### Gradle

Add the following dependency to your `build.gradle`:

```groovy
implementation 'io.github.christianklisch:smartmask-spring-boot-starter:0.3.0'
```

Or if you're using Kotlin DSL (`build.gradle.kts`):

```kotlin
implementation("io.github.christianklisch:smartmask-spring-boot-starter:0.3.0")
```

## Usage

### Basic Usage

1. Add the dependency to your project
2. Annotate fields that contain sensitive data with `@Sensitive`

```java
import io.github.christianklisch.smartmask.annotations.Sensitive;

public class User {
    private Long id;
    private String username;
    
    @Sensitive
    private String password;
    
    @Sensitive(type = MaskType.EMAIL)
    private String email;
    
    @Sensitive(type = MaskType.CREDIT_CARD)
    private String creditCardNumber;
    
    // Getters and setters
}
```

### Customizing Masking

You can customize how the data is masked:

```java
// Show first 3 characters, mask the rest
@Sensitive(showFirst = 3)
private String partiallyVisibleData;

// Show last 4 characters, mask the rest
@Sensitive(showLast = 4)
private String lastFourVisible;

// Use a custom mask character
@Sensitive(maskChar = '#')
private String customMaskChar;
```

### Role-Based Access Control

You can specify which roles are allowed to see the unmasked data:

```java
// Only users with ROLE_ADMIN can see the unmasked value
@Sensitive(rolesAllowed = {"ROLE_ADMIN"})
private String adminOnlyData;

// Multiple roles can be specified
@Sensitive(rolesAllowed = {"ROLE_ADMIN", "ROLE_SUPPORT"})
private String supportData;
```

### Logging

When logging objects with sensitive fields, the library automatically masks those fields:

```java
User user = new User();
user.setUsername("johndoe");
user.setPassword("secret123");
user.setEmail("john.doe@example.com");

// The password and email will be masked in the log
log.info("User created: {}", user);
```

## Available Mask Types

- `MaskType.GENERIC`: Customizable masking (default)
- `MaskType.EMAIL`: Masks the email address while preserving the format (e.g., `j***e@example.com`)
- `MaskType.CREDIT_CARD`: Shows only the last 4 digits (e.g., `************1234`)
- `MaskType.PHONE_NUMBER`: Shows first 3 and last 2 digits (e.g., `123****78`)
- `MaskType.IBAN`: Shows first 4 and last 4 characters (e.g., `DE89************1234`)

## Examples

You can find a complete example project in the [/examples/springboot3](examples/springboot3) directory.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](http://www.apache.org/licenses/LICENSE-2.0.txt) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Code of Conduct

Please note that this project is released with a Contributor Code of Conduct. By participating in this project you agree to abide by its terms. Please read the [Code of Conduct](code-of-conduct.md) for more information.


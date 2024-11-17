# CMS Gateway

## Overview
The CMS Gateway is a Spring Cloud Gateway application serving as a reverse proxy for routing and filtering HTTP requests. It implements secure token validation, advanced routing, and database integration.

## Technologies
- **Java 21**: Base programming language.
- **Spring Boot 3.3.5**: Application framework.
- **Spring Cloud Gateway**: Reverse proxy for routing requests.
- **Spring JDBC**: For database operations.
- **Oracle Database (ojdbc8)**: Backend database.
- **Reactor Netty**: Reactive HTTP client and server.
- **Lombok**: To reduce boilerplate code.

## Features
1. **Routing and Filtering**:
    - Supports dynamic route definitions.
    - Applies global filters for header manipulation.
2. **Token Validation**:
    - Verifies OAuth tokens via the `TokenValidationFilter`.
    - Includes custom logic for obfuscating and hashing tokens for security.
3. **Database Integration**:
    - Executes queries and procedures with `JdbcTemplate` for optimal performance.
4. **Performance Optimizations**:
    - Connection pooling via HikariCP.
    - HTTP/2 support for reduced latency.

## Requirements
- **Java**: 21 or later.
- **Oracle Database**: Version 11.2 or higher.
- **Maven**: For dependency management.
- **Environment Variables**:
    - `CMS_DB_HOST`: Database host.
    - `CMS_DB_SN`: Service name.
    - `CMS_DB_USER`: Database username.
    - `CMS_DB_PASS`: Database password.

## Implementation Details

### Token Validation Filter
The `TokenValidationFilter` validates incoming tokens and enhances requests with metadata.

- **Steps**:
    - Extract `Authorization` and `x-fib-login` headers.
    - Validate tokens using `TokenRepository`.
    - Attach metadata to the request headers.

### Login Token Utility
Handles token generation and verification using:
- **HMAC (SHA256)**: For hashing tokens.
- **Obfuscation**: Adds complexity to tokens via character shifts.

### Database Integration
- Executes database queries and stored procedures using `JdbcTemplate` and `SimpleJdbcCall`.
- Example:
    - Validates tokens from the `TOKENS3` table.
    - Retrieves metadata via the `PKG_META.GET_USER_METADATA` function.

### Run

java -XX:+UseShenandoahGC -Xms8G -Xmx8G -jar target/cms-gateway.jar
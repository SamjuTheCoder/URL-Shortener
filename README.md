# URL Shortener Microservice

A production-ready, RESTful URL shortening service built with Spring Boot. This microservice converts long URLs into short, manageable codes and provides redirection, analytics, and management capabilities.

## 🎯 Project Goal

Create a clean, scalable microservice that:
- Converts long URLs into short codes
- Stores mappings efficiently with persistence
- Provides reliable redirection
- Offers analytics and metadata
- Handles edge cases and errors gracefully
- Supports monitoring and observability
- Can be containerized for easy deployment

## 🚀 Features

### Core Features
- ✅ **URL Shortening**: Convert long URLs to 6-character short codes
- ✅ **Redirection**: HTTP 302 redirects to original URLs
- ✅ **Idempotent Operations**: Same URL returns same short code
- ✅ **Expiration Support**: URLs expire after configurable time
- ✅ **Analytics**: Track click counts and access timestamps
- ✅ **Validation**: URL format and length validation
- ✅ **Collision Handling**: Automatic retry on code collisions

### Advanced Features
- ✅ **Rate Limiting**: Protect against abuse (10 requests/minute)
- ✅ **Monitoring**: Spring Boot Actuator with custom metrics
- ✅ **API Documentation**: OpenAPI 3 with Swagger UI
- ✅ **Container Support**: Docker and Docker Compose
- ✅ **Database Options**: H2 (dev) and PostgreSQL (prod)
- ✅ **Error Handling**: RFC 7807 Problem Details
- ✅ **Scheduled Cleanup**: Automatic removal of expired URLs

## 🛠️ Tech Stack

- **Java 17** - Programming language
- **Spring Boot 3.1.5** - Framework
- **Spring Data JPA** - Persistence
- **H2 Database** - In-memory development database
- **PostgreSQL** - Production database
- **Springdoc OpenAPI** - API documentation
- **Bucket4j** - Rate limiting
- **Micrometer** - Metrics and monitoring
- **JUnit 5 & Mockito** - Testing
- **Testcontainers** - Integration testing
- **Docker** - Containerization


## 🚦 Build & Run Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+ or Gradle
- Docker (optional, for containerized deployment)

### Quick Start


# Clone the repository
git clone <repository-url>
cd url-shortener

# Build the project
./mvnw clean package

# Run tests
./mvnw test

# Run the application
./mvnw spring-boot:run

## 📊 API Documentation

### Interactive Documentation
Once the application is running, access:
- **Swagger UI**: http://localhost:8080/swagger-ui.html

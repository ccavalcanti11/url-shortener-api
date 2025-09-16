📘 Project Title: URL Shortener Service API

![Java](https://img.shields.io/badge/Java-21-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.3-brightgreen.svg)
![MongoDB](https://img.shields.io/badge/MongoDB-Enabled-green.svg)
![Redis](https://img.shields.io/badge/Redis-Caching-red.svg)
![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)

## 📋 Table of Contents
- [🧠 Overview](#-overview)
- [🎯 Features](#-features)
- [🛠️ Tech Stack](#️-tech-stack)
- [📐 Architecture](#-architecture)
- [📦 API Endpoints](#-api-endpoints)
- [🚀 Quick Start](#-quick-start)
- [🧪 Testing](#-testing)
- [📄 Documentation](#-documentation)
- [⚙️ Configuration](#️-configuration)
- [🚢 Deployment](#-deployment)
- [🔧 Development](#-development)
- [💼 Why This Project?](#-why-this-project)
- [🤝 Contributing](#-contributing)
- [📝 License](#-license)

## 🧠 Overview
A concise and scalable RESTful API built with Spring Boot that converts long URLs into short, shareable links. Includes analytics tracking, caching with Redis, and persistence with MongoDB.

## 🎯 Features
- 🔗 Shorten long URLs into compact codes
- 🚀 Redirect users from short URLs to original destinations
- 📊 Track click analytics (e.g., number of visits, timestamps)
- ⚡ Redis caching for fast redirection
- 🗃️ MongoDB for persistent storage
- 📚 Comprehensive API documentation with Swagger
- 🧪 Unit and integration tests
- 🐳 Docker containerization ready

## 🛠️ Tech Stack
| Layer | Technology |
|-------|------------|
| Backend | Java 21 + Spring Boot 3.3.3 |
| Caching | Redis |
| Database | MongoDB |
| Testing | JUnit + Mockito |
| Documentation | Swagger/OpenAPI |
| Deployment | Docker |

## 📐 Architecture
Follows a clean layered structure:
- **Controller**: Handles HTTP requests
- **Service**: Business logic and URL generation
- **Repository**: MongoDB interactions
- **Config**: Redis and Swagger configuration
- **Exception**: Global error handling

## 📦 API Endpoints

### Shorten URL
```http
POST /api/shorten
Content-Type: application/json

{
  "longUrl": "https://example.com"
}
```

**Response:**
```json
{
  "shortUrl": "http://localhost:8080/abc123",
  "originalUrl": "https://example.com",
  "shortCode": "abc123"
}
```

### Redirect to Original URL
```http
GET /{shortCode}
```
→ Returns HTTP 302 redirect to original URL

### Get Analytics
```http
GET /api/analytics/{shortCode}
```

**Response:**
```json
{
  "shortCode": "abc123",
  "originalUrl": "https://example.com",
  "totalClicks": 5,
  "createdAt": "2024-01-15T10:30:00",
  "recentClicks": [...],
  "active": true
}
```

## 🚀 Quick Start

### Prerequisites
- Java 21
- Docker and Docker Compose

### Option 1: Using Docker Compose (Recommended)
```bash
# Clone the repository
git clone <your-repo-url>
cd url-shortener-api

# Start all services (MongoDB, Redis, and the application)
docker-compose up -d

# The application will be available at http://localhost:8080
```

### Option 2: Local Development
```bash
# Start MongoDB and Redis using Docker
docker-compose up -d mongodb redis

# Build and run the application
./gradlew bootRun

# Or build and run the JAR
./gradlew build
java -jar build/libs/urlshortener-*.jar
```

## 🧪 Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Integration Tests
```bash
./gradlew integrationTest
```

### Manual Testing with curl

**Shorten a URL:**
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"longUrl": "https://www.google.com"}'
```

**Test Redirection:**
```bash
curl -I http://localhost:8080/{shortCode}
```

**Get Analytics:**
```bash
curl http://localhost:8080/api/analytics/{shortCode}
```

## 📄 Documentation

### Swagger UI
- **URL**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

### Health Check
- **Actuator Health**: http://localhost:8080/actuator/health

## ⚙️ Configuration

### Application Properties
Key configuration options in `application.properties`:

```properties
# MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=urlshortener

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Application
app.base-url=http://localhost:8080
app.short-code-length=6
```

### Environment Variables for Docker
```yaml
environment:
  SPRING_DATA_MONGODB_HOST: mongodb
  SPRING_DATA_REDIS_HOST: redis
  APP_BASE_URL: http://localhost:8080
```

## 🚢 Deployment

### Docker Build
```bash
# Build the Docker image
docker build -t url-shortener-api .

# Run the container
docker run -p 8080:8080 url-shortener-api
```

### Production Considerations
- Configure proper MongoDB authentication
- Set up Redis password protection
- Use environment-specific configuration
- Configure proper logging levels
- Set up monitoring and health checks

## 🔧 Development

### Project Structure
```
src/
├── main/
│   ├── java/com/carloscavalcanti/urlshortner/
│   │   ├── UrlshortnerApplication.java
│   │   ├── config/           # Configuration classes
│   │   ├── controller/       # REST controllers
│   │   ├── dto/             # Data transfer objects
│   │   ├── exception/       # Exception handling
│   │   ├── model/           # Domain entities
│   │   ├── repository/      # Data access layer
│   │   └── service/         # Business logic
│   └── resources/
│       └── application.properties
└── test/                    # Unit and integration tests
```

## 💼 Why This Project?
Perfect for portfolio and learning:
- ✅ Small scope, big impact
- ✅ Demonstrates backend fundamentals
- ✅ Modern Spring Boot practices
- ✅ Comprehensive testing
- ✅ Production-ready features
- ✅ Easy to clone, run, and understand

## 🤝 Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

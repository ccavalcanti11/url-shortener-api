
📘 Project Title: URL Shortener Service API
      

![Java](https://img.shields.io/badge/Java-21-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.5-brightgreen.svg)
![MongoDB](https://img.shields.io/badge/MongoDB-Enabled-green.svg)
![Redis](https://img.shields.io/badge/Redis-Caching-red.svg)
![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)


🧠 Overview
A concise and scalable RESTful API built with Spring Boot that converts long URLs into short, shareable links. Includes analytics tracking, caching with Redis, and persistence with MongoDB.

🎯 Features
🔗 Shorten long URLs into compact codes

🚀 Redirect users from short URLs to original destinations

📊 Track click analytics (e.g., number of visits, timestamps)

⚡ Redis caching for fast redirection

🗃️ MongoDB for persistent storage

🛠️ Tech Stack
Layer	Technology
Backend	Java + Spring Boot
Caching	Redis
Database	MongoDB
Testing	JUnit + Mockito
Documentation	Swagger/OpenAPI
Deployment	Docker
📐 Architecture
Follows a clean layered structure:

Controller: Handles HTTP requests

Service: Business logic and URL generation

Repository: MongoDB interactions

📦 API Endpoints
http
POST /api/shorten
→ Request: { "longUrl": "https://example.com" }
→ Response: { "shortUrl": "http://short.ly/abc123" }

GET /{shortCode}
→ Redirects to original URL

GET /api/analytics/{shortCode}
→ Returns usage statistics
🧪 Testing
Unit tests with JUnit and Mockito

Integration tests for API endpoints

📄 Documentation
Swagger UI available at /swagger-ui.html

Includes request/response models and error codes

🚢 Deployment
Dockerfile included for containerized setup

Run locally with docker-compose up

💼 Why This Project?
Perfect for freelance showcases:

Small scope, big impact

Demonstrates backend fundamentals

Easy to clone, run, and understand

# 🚗 Vehicle Management System - Copilot Instructions

## Project Overview

This is a **Vehicle Management System** built with **Java Spring Boot MVC** for company fleet booking and administration.

## Key Features

- **Employee Portal**: View and book available vehicles
- **Admin Dashboard**: Manage vehicles and approve bookings
- **Booking Workflow**: Request → Approval → Completion cycle
- **Role-based Access**: Employee vs Admin/Fleet Manager permissions

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.1.4, Spring MVC
- **Database**: MySQL 8.0 with Spring Data JPA
- **Security**: Spring Security for authentication/authorization
- **Frontend**: Thymeleaf templates with Bootstrap 5
- **Build**: Maven

## Core Entities

1. **User** (Employee/Admin with roles)
2. **Vehicle** (Company fleet with status tracking)
3. **Booking** (Reservation requests with approval workflow)

## Development Guidelines

- Follow MVC architecture pattern
- Use proper validation on all forms
- Implement role-based security
- Create responsive, user-friendly interfaces
- Write clear, documented code
- Include proper error handling

## Current Status

✅ Project structure scaffolded
✅ Core entities and repositories created
✅ Basic MVC setup with Thymeleaf
✅ Database configuration ready
🔄 Next: Security configuration and service layer

## File Structure

```
src/main/java/com/company/vehiclemanagement/
├── model/          # JPA entities and enums
├── repository/     # Data access interfaces
├── service/        # Business logic (to be created)
├── controller/     # Web controllers
└── config/         # Security and app configuration
```

Work through development systematically:

1. Configure security and authentication
2. Implement service layer business logic
3. Create comprehensive controllers
4. Build user-friendly Thymeleaf templates
5. Add validation and error handling
6. Test and document features

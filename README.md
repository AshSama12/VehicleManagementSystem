# 🚗 Vehicle Management System

A comprehensive web application for managing company fleet vehicles, bookings, and approvals built with **Spring Boot MVC**.

## 📋 Project Overview

The Vehicle Management System allows company employees to:

- View available vehicles with detailed specifications
- Book vehicles for business trips and purposes
- Track booking status (Pending, Approved, Rejected)
- View booking history and manage requests

Administrators and Fleet Managers can:

- Manage vehicle inventory and details
- Approve or reject booking requests
- Track vehicle usage and maintenance schedules
- Generate reports and analytics

## 🛠 Tech Stack

- **Backend**: Java 17, Spring Boot 3.1.4, Spring MVC
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA, Hibernate
- **Security**: Spring Security (Authentication & Authorization)
- **Frontend**: Thymeleaf Templates, Bootstrap 5, HTML5, CSS3
- **Build Tool**: Maven
- **Development Tools**: Spring Boot DevTools

## 🏗 Project Structure

```
vehicleManagementSystem/
├── src/
│   ├── main/
│   │   ├── java/com/company/vehiclemanagement/
│   │   │   ├── model/           # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Vehicle.java
│   │   │   │   ├── Booking.java
│   │   │   │   └── enums/       # Status and type enums
│   │   │   ├── repository/      # Data Access Layer
│   │   │   ├── service/         # Business Logic Layer
│   │   │   ├── controller/      # Web Controllers
│   │   │   ├── config/          # Security & Configuration
│   │   │   └── VehicleManagementApplication.java
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf HTML templates
│   │       ├── static/          # CSS, JS, Images
│   │       └── application.properties
│   └── test/                    # Unit and Integration Tests
├── pom.xml                      # Maven Dependencies
└── README.md
```

## 🗄 Database Schema

### Core Entities

1. **Users** - Employee and admin information

   - id, username, email, password, firstName, lastName
   - phoneNumber, employeeId, department, role, isActive
   - createdAt, updatedAt

2. **Vehicles** - Company fleet information

   - id, make, model, year, licensePlate, type, fuelType
   - seatingCapacity, mileage, color, status
   - insuranceExpiry, lastServiceDate, nextServiceDate
   - description, createdAt, updatedAt

3. **Bookings** - Vehicle booking requests
   - id, userId, vehicleId, startDateTime, endDateTime
   - destination, purpose, status, approvalNotes
   - approvedBy, approvedAt, createdAt, updatedAt

### Enums

- **Role**: EMPLOYEE, ADMIN, FLEET_MANAGER
- **VehicleType**: SEDAN, SUV, HATCHBACK, TRUCK, VAN, etc.
- **VehicleStatus**: AVAILABLE, IN_USE, MAINTENANCE, OUT_OF_SERVICE
- **BookingStatus**: PENDING, APPROVED, REJECTED, COMPLETED, CANCELLED
- **FuelType**: PETROL, DIESEL, ELECTRIC, HYBRID, CNG, LPG

## 🚀 Getting Started

### Prerequisites

1. **Java 17+** - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
2. **Maven 3.6+** - Download from [Apache Maven](https://maven.apache.org/download.cgi)
3. **MySQL 8.0+** - Download from [MySQL](https://dev.mysql.com/downloads/mysql/)
4. **VS Code** with extensions:
   - Extension Pack for Java
   - Spring Boot Tools
   - Spring Initializr

### Setup Instructions

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd vehicleManagementSystem
   ```

2. **Configure Database**

   - Create MySQL database:
     ```sql
     CREATE DATABASE vehicle_management_db;
     ```
   - Update `src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/vehicle_management_db
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     ```

3. **Build the project**

   ```bash
   ./mvnw clean install
   ```

4. **Run the application**

   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the application**
   - Open browser and go to: `http://localhost:8080`

## 🔧 Development Workflow

### Employee Workflow

1. **Login** → Employee Dashboard
2. **View Available Vehicles** → Filter by type, capacity, etc.
3. **Book Vehicle** → Select dates, destination, purpose
4. **Track Status** → View pending/approved/rejected bookings
5. **View History** → See past bookings and details

### Admin Workflow

1. **Login** → Admin Dashboard
2. **Manage Vehicles** → Add/edit/remove vehicles
3. **Review Bookings** → Approve/reject pending requests
4. **Generate Reports** → Usage statistics, maintenance schedules
5. **User Management** → Manage employee accounts

## 📁 Key Features

### Core Functionality

- ✅ User Authentication & Authorization (Role-based)
- ✅ Vehicle CRUD Operations
- ✅ Booking Request System
- ✅ Approval Workflow
- ✅ Conflict Prevention (Double-booking)
- ✅ Responsive Web Design

### Advanced Features (Future Enhancements)

- 📧 Email Notifications
- 📱 Mobile App Integration
- 🗺 GPS Tracking Integration
- 📊 Advanced Analytics Dashboard
- 🔧 Maintenance Scheduling
- 📱 SMS Notifications

## 🧪 Testing

Run unit tests:

```bash
./mvnw test
```

Run integration tests:

```bash
./mvnw verify
```

## 📝 API Endpoints

### Public Endpoints

- `GET /` - Home page
- `GET /login` - Login page
- `POST /login` - Authentication
- `GET /register` - Registration page
- `POST /register` - User registration

### Employee Endpoints

- `GET /dashboard` - Employee dashboard
- `GET /vehicles` - Available vehicles
- `GET /bookings/new` - New booking form
- `POST /bookings` - Create booking
- `GET /bookings/my` - User's bookings

### Admin Endpoints

- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/vehicles` - Manage vehicles
- `GET /admin/bookings` - Manage bookings
- `PUT /admin/bookings/{id}/approve` - Approve booking
- `PUT /admin/bookings/{id}/reject` - Reject booking

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:

- Create an issue in the repository
- Contact: your-email@company.com

---

**Built with ❤️ using Spring Boot MVC**

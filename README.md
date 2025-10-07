Backend System (Partial Module)
This repository contains a partial public version of the Aadati backend system, available for technical review and demonstration. This repository focuses on specific modules (e.g., security, JWT, and core configurations), while maintaining the privacy of business logic and private components.

## 🎯 Technical Highlights

- **Self-Healing Architecture**: Automated data consistency checks and repairs on system startup
- **150+ RESTful Endpoints**: Fully documented with OpenAPI/Swagger
- **Advanced Security**: JWT-based authentication with role-based access control
- **High Performance**: Optimized with caching, indexing, and connection pooling
- **Domain-Driven Design**: Clean architecture with separation of concerns

---

## 🏗️ System Architecture

### Domain-Driven Design Structure

![WhatsApp Image 2025-10-03 at 23 21 09_47e1e5fe](https://github.com/user-attachments/assets/afe7b15c-36f2-48fb-9030-b5932156a00a)

```
src/main/java/com/aadati/
│
├── 📦 entities/              # 13 Domain Entities
├── 📦 repositories/          # Data Access Layer
├── 📦 services/              # Business Logic Layer
├── 📦 controllers/           # REST API Layer (150+ endpoints)
├── 📦 initialization/        # Self-Healing & Bootstrap Logic
├── 📦 calculation/           # Analytics & Computation Engine
├── 📦 utility/               # Helper Components
├── 📦 enums/                 # Type-safe Enumerations
├── 📦 security/              # Security Configuration
├── 📦 jwt/                   # JWT Implementation
├── 📦 dto/
│   ├── request/              # Request DTOs
│   └── response/             # Response DTOs
├── 📦 mapper/                # MapStruct Mappers
└── 📦 exception/             # Centralized Error Handling
```

---

## ✨ Core Features

### 🔄 Self-Healing Mechanism
Automated data integrity system that runs on every application startup:

**Phase 1: Bootstrap Validation**
- ✓ Verify and create admin user accounts
- ✓ Initialize role hierarchy (ADMIN, USER)
- ✓ Populate reference data tables
- ✓ Configure priority levels

**Phase 2: Data Consistency Checks**
- ✓ Scan for temporal data gaps
- ✓ Validate relational integrity
- ✓ Fill missing calendar entries
- ✓ Repair incomplete records

**Phase 3: Completion Records Healing**
- ✓ Verify tracking records completeness
- ✓ Auto-generate missing entries
- ✓ Synchronize current date records
- ✓ Maintain data continuity

**Phase 4: Analytics Recalculation**
- ✓ Recompute daily metrics
- ✓ Aggregate weekly statistics
- ✓ Update grade classifications
- ✓ Validate calculation accuracy

### ⏰ Scheduled Operations
- **Daily Tasks (00:00)**: Automatic record generation for active entities
- **Annual Tasks**: Calendar year initialization
- **Real-time Updates**: Instant metric recalculation on data changes

### 🔐 Advanced Security Features
- **User Agent Detection**: Device and browser identification for security analytics
- **Session Management**: Comprehensive user session tracking

---

## 🔐 Security Implementation

### JWT Authentication
```java
- Token Generation with configurable expiration
- Secure token validation on every request
- Refresh token support
- Stateless session management
```

### Authorization Strategy
- **Role-Based Access Control (RBAC)**
  - ROLE_ADMIN: Full system access
  - ROLE_USER: Standard user permissions
- **Method-Level Security**: @PreAuthorize annotations
- **Custom UserDetails**: Extended user information handling

### Spring Security 6 Configuration
```java
SecurityFilterChain features:
- Custom JWT Authentication Filter
- CORS Configuration
- CSRF Protection (stateless)
- Exception Handling
```

---

## ⚡ Performance Optimization

### Database Layer
| Optimization | Implementation |
|-------------|----------------|
| **Indexing** | Strategic indexes on frequently queried columns |
| **Connection Pooling** | HikariCP for efficient connection management |
| **Query Optimization** | JPA Specifications & Criteria API |
| **Lazy Loading** | Fetch strategies for optimal performance |

### Advanced Pagination
```java
Pageable Support:
- Customizable page sizes
- Multi-field sorting
- Total count optimization
- Efficient result sets
```

---

### Core Domain
1. **User Management**: User, Role entities
2. **Primary Domain**: 2 main business entities with categorization
3. **Tracking System**: Completion records for both entity types
4. **Calendar Infrastructure**: Day, Week, and Day-of-Week entities
5. **Analytics**: Daily and weekly percentage calculations with grading
6. **Classification**: Category and Priority entities

### Entity Relationships
- One-to-Many: User → Records
- Many-to-Many: User ↔ Roles
- One-to-Many: Calendar → Tracking Records
- Complex Aggregations: Analytics with multiple joins

---

## 🛠️ Technology Stack

### Core Framework
| Technology | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 3.5.4 | Core Framework |
| Java | 21 | Programming Language |
| Spring Security | 6.x | Security Layer |
| JWT (JJWT) | 0.12.6 | Token Authentication |
| PostgreSQL | Latest | Primary Database |
| JPA/Hibernate | Latest | ORM Framework |

### Supporting Libraries
| Library | Version | Purpose |
|---------|---------|---------|
| MapStruct | 1.5.4 | DTO Mapping |
| Lombok | 1.18.38 | Code Generation |
| SpringDoc OpenAPI | 2.2.0 | API Documentation |
| Spring Actuator | Latest | Monitoring & Health Checks |
| Micrometer Prometheus | Latest | Metrics & Monitoring |
| Apache Commons Lang3 | 3.17.0 | Utility Functions |
| Bucket4j | 7.6.0 | Rate Limiting |
| Passay | 1.6.4 | Password Validation |
| MaxMind GeoIP2 | 4.2.1 | IP Geolocation |
| UserAgentUtils | 1.21 | User Agent Parsing |
| UAP-Java | 1.6.0 | Device Detection |
| TestContainers | Latest | Integration Testing |
| H2 Database | Latest | In-Memory Testing Database |
| SonarQube | IDE Plugin | Code Quality Analysis |

---

## 📚 API Documentation

### Swagger Integration

![WhatsApp Image 2025-10-03 at 23 16 45_c3fd97c3](https://github.com/user-attachments/assets/0f72daa0-406e-4344-84fb-b881df93e341)


- **URL**: `http://localhost:8080/swagger-ui.html`
- **Large RESTful API set (Full version includes 150+ endpoints)**
- **Request/Response Examples**
- **Interactive Testing Interface**

### API Categories
```
📁 Authentication & Authorization APIs
📁 User Management APIs  
📁 Primary Domain Management APIs
📁 Secondary Domain Management APIs
📁 Category & Classification APIs
📁 Priority Management APIs
📁 Tracking & Completion APIs
📁 Analytics & Reporting APIs
📁 Calendar & Scheduling APIs
```

---

## 📊 Code Quality & Standards

### SonarQube Integration (IntelliJ IDEA Plugin)

![WhatsApp Image 2025-10-03 at 23 11 10_54d3e549](https://github.com/user-attachments/assets/490d63c3-d3dd-475f-9e0e-aec4b5a03b55)

- ✓ **Real-time Code Analysis**: Instant feedback during development
- ✓ **Bug Detection**: Automatic identification of potential bugs
- ✓ **Security Vulnerabilities**: Detection of security issues
- ✓ **Code Smells**: Maintainability and readability checks
- ✓ **Code Coverage**: Test coverage analysis
- ✓ **Technical Debt**: Measurement and tracking

### Logging Strategy
```java
DEBUG - Detailed execution flow
INFO  - Business operation events
WARN  - Potential issues & anomalies
ERROR - Exceptions & critical failures
```

---

## 🚀 Getting Started

### Prerequisites
```bash
- Java 21 or higher
- PostgreSQL (Latest version)
- Maven 3.8+
- Git
```

2. **Database Configuration**
(Sample config — real credentials are environment-specific and not included)```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/db_name
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. **Build Project**
```bash
mvn clean install
```

4. **Run Application**
```bash
mvn spring-boot:run
```

5. **Access Documentation**
```
http://localhost:8080/swagger-ui.html
```
---

## 🔧 Configuration

### Application Properties
```properties
# Server Configuration
server.port=8080

# JWT Settings
jwt.secret=your-secret-key
jwt.expiration=86400000

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/db_name
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Monitoring
management.endpoints.web.exposure.include=health,info,metrics
```

---

## 📈 System Flows

### Daily Operation Cycle
```
00:00 Scheduled Task
    ↓
Generate Calendar Entries
    ↓
Create Tracking Records
    ↓
Calculate Daily Metrics
    ↓
Aggregate Weekly Statistics
```

### Self-Healing Process
```
System Startup
    ↓
Bootstrap Data Check → Add if Missing
    ↓
Calendar Integrity Check → Fill Gaps
    ↓
Completion Records Check → Generate Missing
    ↓
Analytics Validation → Recalculate
    ↓
System Ready
```
---

## 🤝 Development Standards

### Code Style
- Follow Spring Boot best practices
- Clean Code principles
- Comprehensive JavaDoc comments


---
## 👨‍💻 Author
**Mohamed Taha Elmeligy**
- GitHub:[ [@yourusername](https://github.com/mohamed-taha-elmeligy)](https://github.com/mohamed-taha-elmeligy/)
- LinkedIn: [[Your Profile](https://linkedin.com/inmtelmeligy-backend-dev)](https://www.linkedin.com/in/mtelmeligy-backend-dev/)
---

<div align="center">

**Built with Spring Boot & Best Practices**

⭐ Star this repository if you find it helpful! ⭐

</div>

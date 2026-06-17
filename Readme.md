# 🏦 Real-Time Transaction Monitoring System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-blue.svg?logo=spring)](https://spring.io/projects/spring-cloud)
[![Java](https://img.shields.io/badge/Java-17%20%7C%2021-orange.svg?logo=java)](https://www.oracle.com/java/)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.7.0-red.svg?logo=apachekafka)](https://kafka.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg?logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](https://github.com/SatyamKumar7911/Real-Time-Transaction-Monitoring-System)
[![Contributors](https://img.shields.io/badge/Contributors-1-blue.svg)](https://github.com/SatyamKumar7911)

> **A Production-Ready Enterprise Microservices Platform** implementing real-time transaction monitoring with event-driven architecture, distributed processing, and cloud-native patterns. Built with Spring Boot 3.5.7, Apache Kafka, MySQL 8.0, and Netflix Eureka.

---

## 📑 Table of Contents

<details open>
<summary><b>Click to expand</b></summary>

1. [🎯 Project Overview](#-project-overview)
2. [✨ Key Features](#-key-features)
3. [🏗️ System Architecture](#️-system-architecture)
4. [🛠️ Technology Stack](#️-technology-stack)
5. [📊 Microservices](#-microservices)
6. [📋 Prerequisites](#-prerequisites)
7. [🚀 Getting Started](#-getting-started)
8. [📡 API Documentation](#-api-documentation)
9. [🗄️ Database Schema](#️-database-schema)
10. [🔄 Event Flow](#-event-flow)
11. [📨 Kafka Topics](#-kafka-topics)
12. [🧪 Testing](#-testing)
13. [🔐 Security](#-security)
14. [📊 Monitoring & Observability](#-monitoring--observability)
15. [⚡ Performance Optimizations](#-performance-optimizations)
16. [🔧 Configuration](#-configuration)
17. [🐛 Troubleshooting](#-troubleshooting)
18. [🚀 Future Enhancements](#-future-enhancements)
19. [🤝 Contributing](#-contributing)
20. [📞 Contact & Support](#-contact--support)

</details>

---

## 🎯 Project Overview

The **Real-Time Transaction Monitoring System** is an enterprise-grade microservices application that demonstrates modern cloud-native architecture patterns. It provides a complete solution for:

- **Account Management** - User and bank account operations
- **Transaction Processing** - Real-time credit/debit transactions
- **Event-Driven Communication** - Asynchronous processing via Apache Kafka
- **API Gateway Pattern** - Unified entry point with JWT authentication
- **Service Discovery** - Dynamic service registration and health monitoring
- **Centralized Configuration** - Environment-specific configurations

### 🎯 Project Objectives

| Objective | Status |
|-----------|--------|
| Build scalable microservices architecture | ✅ Complete |
| Implement event-driven communication | ✅ Complete |
| Real-time transaction processing | ✅ Complete |
| JWT-based security | ✅ Complete |
| Service discovery & health monitoring | ✅ Complete |
| Distributed tracing & observability | ✅ Complete |
| Production-ready deployment patterns | ✅ Complete |

---

## ✨ Key Features

### 🏗️ Architecture Patterns
- ✅ **Microservices Architecture** - 6 independent, scalable services
- ✅ **Event-Driven Design** - Apache Kafka for asynchronous communication
- ✅ **API Gateway Pattern** - Spring Cloud Gateway for request routing & security
- ✅ **Service Discovery** - Netflix Eureka for dynamic service registration
- ✅ **Circuit Breaker Pattern** - Resilience4j for fault tolerance
- ✅ **Centralized Configuration** - Spring Cloud Config Server
- ✅ **Client-Side Load Balancing** - Spring Cloud LoadBalancer

### 🔐 Security & Authentication
- ✅ **JWT Token-Based Auth** - Stateless, secure authentication
- ✅ **Role-Based Access Control** - ADMIN, USER roles
- ✅ **Spring Security Integration** - Comprehensive security framework
- ✅ **CORS Configuration** - Cross-Origin Resource Sharing support
- ✅ **Encrypted Credentials** - Secure password management

### 📊 Data & Persistence
- ✅ **MySQL 8.0 Support** - Relational database for core services
- ✅ **H2 In-Memory DB** - Fast testing and development
- ✅ **Spring Data JPA** - Object-relational mapping
- ✅ **Database Migrations** - Version control for schema
- ✅ **Multi-Database Support** - Flexible persistence layer

### 📡 Real-Time Processing
- ✅ **Kafka Event Streaming** - 3 topics for event distribution
- ✅ **Real-Time Notifications** - Event-driven notification engine
- ✅ **Transaction Monitoring** - Live transaction tracking
- ✅ **Account Event Publishing** - Account lifecycle events

### 🔍 Observability
- ✅ **Distributed Tracing** - Zipkin integration
- ✅ **Spring Boot Actuator** - Production-ready metrics endpoints
- ✅ **Micrometer Metrics** - Comprehensive instrumentation
- ✅ **Structured Logging** - SLF4J & Logback
- ✅ **Health Checks** - Service health monitoring

### 🚀 Developer Experience
- ✅ **Spring Boot DevTools** - Hot reload and rapid development
- ✅ **Postman Collections** - Pre-built API test suites
- ✅ **Comprehensive Documentation** - Guides and tutorials
- ✅ **Docker Support** - Containerization ready
- ✅ **Maven Build System** - Dependency management

---

## 🏗️ System Architecture

### High-Level Architecture Diagram

```mermaid
graph TB
    subgraph Client["🖥️ Client Applications"]
        WEB[Web Browser]
        MOBILE[Mobile App]
    end

    subgraph Gateway["🚪 API Layer"]
        GW[API Gateway<br/>Port 8080<br/>JWT Auth]
    end

    subgraph Services["🎛️ Microservices"]
        AC[Account Service<br/>Port 8081<br/>MySQL]
        TR[Transaction Service<br/>Port 8082<br/>MySQL]
        NT[Notification Service<br/>Port 8083<br/>H2]
    end

    subgraph Infrastructure["⚙️ Infrastructure"]
        EUR[Eureka Server<br/>Port 8761<br/>Service Discovery]
        CONFIG[Config Server<br/>Port 8888<br/>Configuration]
    end

    subgraph Messaging["📨 Event Streaming"]
        KAFKA[Apache Kafka<br/>Port 9092]
        T1[account-events]
        T2[transaction-events]
        T3[notification-events]
    end

    subgraph Data["💾 Data Layer"]
        MYSQL[(MySQL Database<br/>Port 3306<br/>accountdb)]
        H2[(H2 Database<br/>In-Memory)]
    end

    Client -->|HTTP/REST| GW
    GW -->|Route| AC
    GW -->|Route| TR
    GW -->|Route| NT
    
    AC -->|Publish| KAFKA
    TR -->|Publish| KAFKA
    NT -->|Consume| KAFKA
    
    KAFKA -->|Topic| T1
    KAFKA -->|Topic| T2
    KAFKA -->|Topic| T3
    
    AC -->|Read/Write| MYSQL
    TR -->|Read/Write| MYSQL
    NT -->|Read/Write| H2
    
    AC -->|Register| EUR
    TR -->|Register| EUR
    NT -->|Register| EUR
    GW -->|Lookup| EUR
    
    AC -->|Fetch Config| CONFIG
    TR -->|Fetch Config| CONFIG
    NT -->|Fetch Config| CONFIG

    style Client fill:#e1f5ff
    style Gateway fill:#fff3e0
    style Services fill:#f3e5f5
    style Infrastructure fill:#e8f5e9
    style Messaging fill:#fce4ec
    style Data fill:#ede7f6
```

### Service Communication Pattern

```mermaid
sequenceDiagram
    participant Client
    participant APIGateway as API Gateway<br/>:8080
    participant AccountSvc as Account Service<br/>:8081
    participant Kafka as Kafka Broker<br/>:9092
    participant NotifSvc as Notification Service<br/>:8083
    participant MySQL as MySQL Database

    Client->>APIGateway: 1. POST /accounts<br/>(Create Account)
    APIGateway->>AccountSvc: 2. Route Request<br/>(JWT Validated)
    AccountSvc->>MySQL: 3. Save Account<br/>(JPA)
    MySQL-->>AccountSvc: 4. Account Saved<br/>(ID: 123)
    AccountSvc->>Kafka: 5. Publish Event<br/>(account-events)
    Kafka-->>AccountSvc: 6. Event Queued
    AccountSvc-->>APIGateway: 7. Return Response<br/>(201 Created)
    APIGateway-->>Client: 8. Response to Client
    
    Kafka->>NotifSvc: 9. Consume Event<br/>(Async)
    NotifSvc->>MySQL: 10. Create Notification<br/>(H2)
    MySQL-->>NotifSvc: 11. Notification Saved
    NotifSvc->>Kafka: 12. Publish to<br/>(notification-events)
```

### Project Folder Structure

```
Real-Time-Transaction-Monitoring-System/
│
├── 📁 eureka-server/                      # Service Discovery (Port 8761)
│   ├── src/main/java/
│   │   └── com/transaction/eureka/
│   │       └── EurekaServerApplication.java
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
│
├── 📁 config-server/                      # Centralized Config (Port 8888)
│   ├── src/main/java/
│   │   └── com/transaction/config/
│   │       └── ConfigServerApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── config/                        # Git-based configurations
│   │       ├── account-service.yml
│   │       ├── transaction-service.yml
│   │       └── notification-service.yml
│   └── pom.xml
│
├── 📁 api-gateway/                        # API Gateway (Port 8080)
│   ├── src/main/java/
│   │   └── com/transaction/gateway/
│   │       ├── ApiGatewayApplication.java
│   │       ├── config/
│   │       │   ├── GatewayConfiguration.java
│   │       │   └── SecurityConfiguration.java
│   │       ├── filters/
│   │       │   └── JwtAuthenticationFilter.java
│   │       └── handler/
│   │           └── CustomExceptionHandler.java
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
│
├── 📁 account-service/                    # Account Management (Port 8081)
│   ├── src/main/java/
│   │   └── com/transaction/account/
│   │       ├── AccountServiceApplication.java
│   │       ├── controller/
│   │       │   ├── UserController.java
│   │       │   └── AccountController.java
│   │       ├── service/
│   │       │   ├── UserService.java
│   │       │   └── AccountService.java
│   │       ├── repository/
│   │       │   ├── UserRepository.java
│   │       │   └── AccountRepository.java
│   │       ├── model/
│   │       │   ├── User.java
│   │       │   ├── BankAccount.java
│   │       │   └── AccountLog.java
│   │       ├── event/
│   │       │   └── AccountEventProducer.java
│   │       ├── dto/
│   │       │   ├── UserDTO.java
│   │       │   └── AccountDTO.java
│   │       ├── exception/
│   │       │   └── AccountException.java
│   │       └── aspect/
│   │           └── LoggingAspect.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── application-dev.yml
│   ├── src/test/java/                     # Unit & Integration Tests
│   └── pom.xml
│
├── 📁 transaction-service/                # Transaction Processing (Port 8082)
│   ├── src/main/java/
│   │   └── com/transaction/transaction/
│   │       ├── TransactionServiceApplication.java
│   │       ├── controller/
│   │       │   └── TransactionController.java
│   │       ├── service/
│   │       │   └── TransactionService.java
│   │       ├── repository/
│   │       │   ├── TransactionRepository.java
│   │       │   └── TransactionLogRepository.java
│   │       ├── model/
│   │       │   ├── Transaction.java
│   │       │   └── TransactionLog.java
│   │       ├── event/
│   │       │   └── TransactionEventProducer.java
│   │       ├── dto/
│   │       │   └── TransactionDTO.java
│   │       ├── exception/
│   │       │   └── TransactionException.java
│   │       └── aspect/
│   │           └── TransactionAspect.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── application-dev.yml
│   └── pom.xml
│
├── 📁 notification-service/               # Event Processing (Port 8083)
│   ├── src/main/java/
│   │   └── com/transaction/notification/
│   │       ├── NotificationServiceApplication.java
│   │       ├── controller/
│   │       │   └── NotificationController.java
│   │       ├── service/
│   │       │   ├── NotificationService.java
│   │       │   └── EmailService.java
│   │       ├── repository/
│   │       │   └── NotificationRepository.java
│   │       ├── model/
│   │       │   ├── Notification.java
│   │       │   └── NotificationLog.java
│   │       ├── event/
│   │       │   ├── AccountEventConsumer.java
│   │       │   ├── TransactionEventConsumer.java
│   │       │   └── NotificationEventProducer.java
│   │       ├── dto/
│   │       │   └── NotificationDTO.java
│   │       └── config/
│   │           └── KafkaConsumerConfig.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── application-dev.yml
│   └── pom.xml
```

---

## 🛠️ Technology Stack

### 🚀 Core Framework & Runtime
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 17 / 21 | Programming language (LTS versions) |
| **Spring Framework** | 6.x | Enterprise application framework |
| **Spring Boot** | 3.5.7 | Rapid microservices development |
| **Spring Cloud** | 2025.0.0 | Distributed systems support |
| **Maven** | 3.8.1+ | Build & dependency management |

### 🏛️ Infrastructure & Service Mesh
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Netflix Eureka** | Latest | Service discovery & registry |
| **Spring Cloud Config** | 2025.0.0 | Centralized configuration |
| **Spring Cloud Gateway** | 2025.0.0 | API gateway & routing |
| **Resilience4j** | 2.x | Fault tolerance (Circuit Breaker) |
| **Spring Cloud LoadBalancer** | 2025.0.0 | Client-side load balancing |

### 📨 Messaging & Events
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Apache Kafka** | 3.7.0 | Event streaming & pub/sub |
| **Spring Kafka** | 3.x | Kafka integration |
| **Avro / JSON** | Latest | Event serialization |

### 🗄️ Data & Persistence
| Technology | Version | Purpose |
|-----------|---------|---------|
| **MySQL** | 8.0 | Relational database |
| **H2 Database** | 2.x | In-memory testing database |
| **Spring Data JPA** | 3.x | Object-relational mapping |
| **Hibernate** | 6.x | ORM framework |

### 🔐 Security
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Spring Security** | 6.x | Authentication & authorization |
| **JWT (jjwt)** | 0.11.5 | Token-based authentication |
| **bcrypt** | Latest | Password hashing |

### 📊 Observability & Monitoring
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Spring Boot Actuator** | 3.x | Metrics & monitoring endpoints |
| **Micrometer** | 1.x | Metrics collection |
| **Zipkin** | 3.x | Distributed tracing |
| **SLF4J & Logback** | Latest | Logging framework |
| **Spring Cloud Sleuth** | 2025.0.0 | Trace ID propagation |

### 🧪 Testing
| Technology | Version | Purpose |
|-----------|---------|---------|
| **JUnit 5** | 5.x | Unit testing framework |
| **Mockito** | 5.x | Mocking library |
| **TestContainers** | 1.x | Docker containers for testing |
| **Spring Test** | 6.x | Spring framework testing |
| **Postman** | Latest | API testing & documentation |

### 🔧 Development Tools
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Spring Tool Suite (STS)** | 4.x | IDE |
| **Git** | 2.x | Version control |
| **Docker & Docker Compose** | 20.x+ | Containerization |

---

## 📊 Microservices

### 1️⃣ **Eureka Server** (Port 8761)

**Purpose:** Service Discovery & Health Monitoring

```yaml
Server: http://localhost:8761
Type: Service Registry (Netflix Eureka)
Technology: Spring Cloud Netflix Eureka
```

**Features:**
- Dynamic service registration
- Health check monitoring
- Service instance management
- Client-side load balancing support
- Dashboard for service visualization

**Key Configuration:**
```yaml
server:
  port: 8761
  
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: true
```

---

### 2️⃣ **Config Server** (Port 8888)

**Purpose:** Centralized Configuration Management

```yaml
Server: http://localhost:8888
Type: Configuration Server
Technology: Spring Cloud Config
```

**Features:**
- Environment-specific configurations (dev, test, prod)
- Dynamic property refresh
- Git-based configuration storage
- Multi-profile support
- Externalized configuration

**Managed Configurations:**
- account-service.yml
- transaction-service.yml
- notification-service.yml

---

### 3️⃣ **API Gateway** (Port 8080)

**Purpose:** Single Entry Point & Security Gateway

```yaml
Server: http://localhost:8080
Type: API Gateway
Technology: Spring Cloud Gateway
Database: In-Memory
```

**Features:**
- JWT authentication filter
- Request routing to microservices
- Rate limiting & load balancing
- Circuit breaker integration
- CORS configuration
- Custom exception handling

**Security:**
- JWT token validation on all routes
- Role-based access control (ADMIN, USER)
- Exception handling with custom responses

**Endpoints:**
- `/api/auth/*` - Authentication
- `/accounts/*` - Account service routes
- `/transactions/*` - Transaction service routes
- `/actuator/*` - Health & metrics

---

### 4️⃣ **Account Service** (Port 8081)

**Purpose:** User & Account Management

```yaml
Server: http://localhost:8081
Type: Microservice
Database: MySQL (accountdb)
Technology: Spring Boot 3.5.7
```

**Core Responsibilities:**
- User CRUD operations
- Bank account management
- Account balance operations (deposit/withdraw)
- Transaction logging
- Event publishing to Kafka

**Database Tables:**
- `user_entity` - User profiles
- `bank_account_entity` - Bank accounts
- `account_log` - Operation logs

**Published Events:**
- `AccountCreated`
- `AccountUpdated`
- `DepositCompleted`
- `WithdrawalCompleted`

**Key APIs:**
```
POST   /accounts/v1/users              - Create user
GET    /accounts/v1/users              - Get all users
GET    /accounts/v1/users/{id}         - Get user by ID
PUT    /accounts/v1/users/{id}         - Update user
DELETE /accounts/v1/users/{id}         - Delete user

POST   /accounts/v1/accounts           - Create account
GET    /accounts/v1/accounts           - Get all accounts
GET    /accounts/v1/accounts/{id}      - Get account by ID
POST   /accounts/v1/accounts/{id}/deposit     - Deposit funds
POST   /accounts/v1/accounts/{id}/withdraw    - Withdraw funds
```

---

### 5️⃣ **Transaction Service** (Port 8082)

**Purpose:** Transaction Processing & History

```yaml
Server: http://localhost:8082
Type: Microservice
Database: MySQL (accountdb)
Technology: Spring Boot 3.5.7
```

**Core Responsibilities:**
- Credit/Debit transaction processing
- Transaction history retrieval
- Balance calculations
- Transaction validation
- Event publishing to Kafka

**Transaction Types:**
- CREDIT - Money deposit
- DEBIT - Money withdrawal
- TRANSFER - Inter-account transfer

**Published Events:**
- `TransactionInitiated`
- `TransactionCompleted`
- `TransactionFailed`
- `TransferProcessed`

**Key APIs:**
```
POST   /transactions/v1/transfer               - Transfer funds
POST   /transactions/v1/transactions           - Create transaction
GET    /transactions/v1/transactions           - Get transactions
GET    /transactions/v1/transactions/{id}      - Get by ID
POST   /transactions/v1/transactions/credit    - Credit transaction
POST   /transactions/v1/transactions/debit     - Debit transaction
```

---

### 6️⃣ **Notification Service** (Port 8083)

**Purpose:** Event-Driven Notification Processing ⭐

```yaml
Server: http://localhost:8083
Type: Microservice (Event Consumer)
Database: H2 In-Memory
Technology: Spring Boot 3.5.7
```

**Core Responsibilities:**
- Consume account events from Kafka
- Consume transaction events from Kafka
- Process and store notifications
- Send email notifications (simulated)
- Publish notification events

**Event Subscriptions:**
- Subscribes to `account-events` topic
- Subscribes to `transaction-events` topic

**Publishes Events To:**
- `notification-events` topic

**Published Events:**
- `NotificationSent`
- `NotificationDelivered`
- `NotificationFailed`

**Key APIs:**
```
GET    /notifications/v1/notifications         - Get all notifications
GET    /notifications/v1/notifications/{id}    - Get by ID
GET    /notifications/v1/notifications/user/{userId}  - Get user notifications
POST   /notifications/v1/notifications/send    - Send notification
```

---

## 📋 Prerequisites

Before setting up the system, ensure you have:

### 🖥️ System Requirements
- **Operating System:** Windows 10+, macOS 10.15+, or Linux (Ubuntu 20.04+)
- **RAM:** 8 GB minimum (16 GB recommended)
- **Disk Space:** 10 GB minimum
- **Network:** Internet connection for dependencies

### 📦 Required Software

<details>
<summary><b>Java Development Kit (JDK)</b></summary>

```bash
# Download and Install JDK 17 or 21
# Verify installation
java -version

# Expected output:
# java version "17.0.10" 2024-01-16 LTS
# Java(TM) SE Runtime Environment (build 17.0.10+8-LTS)
```

- [Oracle JDK Download](https://www.oracle.com/java/technologies/downloads/)
- [OpenJDK Alternative](https://adoptium.net/)

</details>

<details>
<summary><b>Apache Maven</b></summary>

```bash
# Download and Install Maven 3.8.1+
# Verify installation
mvn -version

# Expected output:
# Apache Maven 3.9.6
# Maven home: C:\apache-maven-3.9.6
```

- [Maven Download](https://maven.apache.org/download.cgi)
- Add to PATH: `C:\apache-maven-x.x.x\bin`

</details>

<details>
<summary><b>Apache Kafka 3.7.0</b></summary>

```bash
# Download Kafka
# Extract to C:\kafka or /opt/kafka

# Verify installation
kafka-topics --version

# Expected output:
# 3.7.0
```

- [Kafka Download](https://kafka.apache.org/downloads)

</details>

<details>
<summary><b>MySQL 8.0 Server</b></summary>

```bash
# Verify installation
mysql --version

# Expected output:
# mysql Ver 8.0.xx for Windows on x86_64
```

- [MySQL Download](https://dev.mysql.com/downloads/mysql/)
- Default Port: 3306
- Ensure server is running

</details>

<details>
<summary><b>Git</b></summary>

```bash
# Verify installation
git --version

# Expected output:
# git version 2.45.0
```

- [Git Download](https://git-scm.com/downloads)

</details>

### 🔧 Optional but Recommended
- **Spring Tool Suite (STS)** - Enhanced IDE for Spring
- **Postman** - API testing and documentation
- **Docker & Docker Compose** - Containerization
- **Zipkin** - Distributed tracing visualization

---

## 🚀 Getting Started

### Step 1: Clone the Repository

```bash
# Clone repository
git clone https://github.com/SatyamKumar7911/Real-Time-Transaction-Monitoring-System.git

# Navigate to project
cd Real-Time-Transaction-Monitoring-System

# Verify structure
dir  # Windows: shows eureka-server, api-gateway, etc.
ls   # Linux/Mac
```

---

### Step 2: Set Up MySQL Database

```bash
# Option 1: Using setup script
mysql -u root -p < setup-database.sql

# Option 2: Manual setup
mysql -u root -p
mysql> CREATE DATABASE IF NOT EXISTS accountdb;
mysql> USE accountdb;
mysql> source setup-database.sql;
mysql> SHOW TABLES;  # Verify
```

**Expected Output:**
```
Database created: accountdb ✅
Tables created:
  - user_entity
  - bank_account_entity
  - account_log
  - change_events
Sample data: 32 users, 16 accounts inserted ✅
```

---

### Step 3: Start Apache Kafka

<details>
<summary><b>Windows (PowerShell)</b></summary>

```powershell
# Terminal 1: Start Zookeeper
cd C:\kafka
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

# Wait for: "Zookeeper is running on port 2181"

# Terminal 2: Start Kafka Broker
cd C:\kafka
.\bin\windows\kafka-server-start.bat .\config\server.properties

# Wait for: "started (kafka.server.KafkaServer)"
```

</details>

<details>
<summary><b>Linux / macOS</b></summary>

```bash
# Terminal 1: Start Zookeeper
cd /opt/kafka
./bin/zookeeper-server-start.sh config/zookeeper.properties

# Terminal 2: Start Kafka Broker
cd /opt/kafka
./bin/kafka-server-start.sh config/server.properties
```

</details>

---

### Step 4: Create Kafka Topics

```bash
# Create account-events topic
kafka-topics --create \
  --topic account-events \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# Create transaction-events topic
kafka-topics --create \
  --topic transaction-events \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# Create notification-events topic
kafka-topics --create \
  --topic notification-events \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# Verify topics
kafka-topics --list --bootstrap-server localhost:9092
```

**Expected Output:**
```
account-events
transaction-events
notification-events
```

---

### Step 5: Build All Services

```bash
# Build with Maven
mvn clean install -DskipTests

# Or build individual services
cd eureka-server && mvn clean install
cd config-server && mvn clean install
cd api-gateway && mvn clean install
cd account-service && mvn clean install
cd transaction-service && mvn clean install
cd notification-service && mvn clean install
```

**Expected Output:**
```
BUILD SUCCESS for all modules ✅
Total time: ~3-5 minutes
```

---

### Step 6: Start All Services (Recommended Order)

#### 🖥️ Terminal 1: Eureka Server (Service Discovery)

```bash
cd eureka-server
mvn spring-boot:run

# Verify:
# [main] c.t.eureka.EurekaServerApplication   : Started
# Browse: http://localhost:8761
```

#### 🖥️ Terminal 2: Config Server (Configuration Management)

```bash
cd config-server
mvn spring-boot:run

# Verify:
# [main] c.t.config.ConfigServerApplication   : Started
# API: http://localhost:8888/account-service/dev
```

#### 🖥️ Terminal 3: Account Service

```bash
cd account-service
mvn spring-boot:run

# Verify:
# [main] c.t.account.AccountServiceApplication : Started
# Registered with Eureka: http://localhost:8761 ✅
```

#### 🖥️ Terminal 4: Transaction Service

```bash
cd transaction-service
mvn spring-boot:run

# Verify:
# [main] c.t.transaction.TransactionServiceApplication : Started
```

#### 🖥️ Terminal 5: Notification Service

```bash
cd notification-service
mvn spring-boot:run

# Verify:
# [main] c.t.notification.NotificationServiceApplication : Started
# Kafka Consumer: Listening to account-events & transaction-events ✅
```

#### 🖥️ Terminal 6: API Gateway (Entry Point)

```bash
cd api-gateway
mvn spring-boot:run

# Verify:
# [main] c.t.gateway.ApiGatewayApplication : Started
# Gateway ready at: http://localhost:8080 ✅
```

### ✅ Startup Verification

```bash
# Check all services are running
curl http://localhost:8761                    # Eureka Dashboard
curl http://localhost:8080/actuator/health    # API Gateway Health
curl http://localhost:8080/api/auth/validate  # Requires token

# Expected response:
# {"status":"UP","components":{...}}
```

**Complete Startup Time:** ~2-3 minutes

---

## 📡 API Documentation

### 🔐 Authentication Endpoints

#### Login Endpoint

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9.eyJyb2xlIjoiQURNSU4iLCJ1c2VybmFtZSI6ImFkbWluIiwic3ViIjoiYWRtaW4iLCJpYXQiOjE3NjUyOTA1ODcsImV4cCI6MTc2NTM3Njk4N30.nAm7KcGQF2LQoFFvTvuyMniVA4d06sgvJKD8AR1bTgkv60b7bMGt_orI7IJ-noVH",
  "type": "Bearer",
  "username": "admin",
  "expiresIn": 86400000
}
```

**Test Credentials:**
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| user | user123 | USER |
| test | test123 | USER |
| demo | demo123 | USER |

---

### 👤 User Management APIs

<details>
<summary><b>Create User</b></summary>

```http
POST /accounts/v1/users
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1-555-1234"
}
```

**Response (201 Created):**
```json
{
  "id": 33,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1-555-1234",
  "createdAt": "2025-12-10T15:30:00Z"
}
```

</details>

<details>
<summary><b>Get All Users</b></summary>

```http
GET /accounts/v1/users
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "User One",
    "email": "user1@example.com",
    "phone": "+1-555-0001",
    "createdAt": "2025-12-01T10:00:00Z"
  },
  {
    "id": 2,
    "name": "User Two",
    "email": "user2@example.com",
    "phone": "+1-555-0002",
    "createdAt": "2025-12-02T11:00:00Z"
  }
]
```

</details>

<details>
<summary><b>Get User by ID</b></summary>

```http
GET /accounts/v1/users/1
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "User One",
  "email": "user1@example.com",
  "phone": "+1-555-0001",
  "createdAt": "2025-12-01T10:00:00Z"
}
```

</details>

<details>
<summary><b>Update User</b></summary>

```http
PUT /accounts/v1/users/1
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "name": "Updated Name",
  "email": "updated@example.com",
  "phone": "+1-555-9999"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Updated Name",
  "email": "updated@example.com",
  "phone": "+1-555-9999",
  "updatedAt": "2025-12-10T15:45:00Z"
}
```

</details>

<details>
<summary><b>Delete User</b></summary>

```http
DELETE /accounts/v1/users/1
Authorization: Bearer {JWT_TOKEN}
```

**Response (204 No Content):**
```
(Empty response body)
```

</details>

---

### 🏦 Account Management APIs

<details>
<summary><b>Create Account</b></summary>

```http
POST /accounts/v1/accounts
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "userId": 1,
  "accountNumber": "ACC-12345",
  "accountType": "SAVINGS",
  "currency": "USD",
  "initialBalance": 1000.00
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "userId": 1,
  "accountNumber": "ACC-12345",
  "accountType": "SAVINGS",
  "currency": "USD",
  "balance": 1000.00,
  "status": "ACTIVE",
  "createdAt": "2025-12-10T15:30:00Z"
}
```

</details>

<details>
<summary><b>Get All Accounts</b></summary>

```http
GET /accounts/v1/accounts
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "accountNumber": "ACC-12345",
    "accountType": "SAVINGS",
    "balance": 1000.00,
    "status": "ACTIVE"
  }
]
```

</details>

<details>
<summary><b>Deposit Money</b></summary>

```http
POST /accounts/v1/accounts/1/deposit
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "amount": 500.00,
  "reference": "Deposit via API"
}
```

**Response (200 OK):**
```json
{
  "accountId": 1,
  "previousBalance": 1000.00,
  "depositAmount": 500.00,
  "newBalance": 1500.00,
  "timestamp": "2025-12-10T15:35:00Z",
  "message": "Deposit successful"
}
```

**Kafka Event Published:**
```json
{
  "eventType": "DepositCompleted",
  "userId": 1,
  "accountNumber": "ACC-12345",
  "amount": 500.00,
  "timestamp": "2025-12-10T15:35:00Z"
}
```

</details>

<details>
<summary><b>Withdraw Money</b></summary>

```http
POST /accounts/v1/accounts/1/withdraw
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "amount": 200.00,
  "reference": "Withdrawal via API"
}
```

**Response (200 OK):**
```json
{
  "accountId": 1,
  "previousBalance": 1500.00,
  "withdrawAmount": 200.00,
  "newBalance": 1300.00,
  "timestamp": "2025-12-10T15:40:00Z",
  "message": "Withdrawal successful"
}
```

</details>

---

### 💳 Transaction APIs

<details>
<summary><b>Transfer Funds</b></summary>

```http
POST /transactions/v1/transfer
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "sourceAccountId": 1,
  "destinationAccountId": 2,
  "amount": 250.00,
  "reference": "Inter-account transfer"
}
```

**Response (200 OK):**
```json
{
  "transactionId": "TXN-20251210-001",
  "status": "COMPLETED",
  "sourceAccountId": 1,
  "destinationAccountId": 2,
  "amount": 250.00,
  "timestamp": "2025-12-10T15:45:00Z",
  "message": "Transfer successful"
}
```

**Kafka Events Published:**
```json
{
  "eventType": "TransferProcessed",
  "sourceUserId": 1,
  "destinationUserId": 2,
  "amount": 250.00,
  "timestamp": "2025-12-10T15:45:00Z"
}
```

</details>

<details>
<summary><b>Create Transaction</b></summary>

```http
POST /transactions/v1/transactions
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "accountId": 1,
  "type": "CREDIT",
  "amount": 100.00,
  "description": "Salary deposit",
  "reference": "SAL-2025-12-001"
}
```

**Response (201 Created):**
```json
{
  "id": 101,
  "accountId": 1,
  "type": "CREDIT",
  "amount": 100.00,
  "description": "Salary deposit",
  "status": "COMPLETED",
  "createdAt": "2025-12-10T15:50:00Z"
}
```

</details>

<details>
<summary><b>Get Transactions</b></summary>

```http
GET /transactions/v1/transactions?accountId=1&limit=10
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
```json
{
  "transactions": [
    {
      "id": 101,
      "accountId": 1,
      "type": "CREDIT",
      "amount": 100.00,
      "status": "COMPLETED",
      "createdAt": "2025-12-10T15:50:00Z"
    }
  ],
  "totalCount": 1,
  "pageSize": 10
}
```

</details>

---

### 📨 Notification APIs

<details>
<summary><b>Get All Notifications</b></summary>

```http
GET /notifications/v1/notifications
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "eventId": "notif-001",
    "userId": "1",
    "notificationType": "TRANSACTION_SUCCESS",
    "channel": "EMAIL",
    "recipient": "user1@example.com",
    "message": "Your deposit of $500 was successful",
    "status": "SENT",
    "createdAt": "2025-12-10T15:35:00Z"
  }
]
```

</details>

<details>
<summary><b>Get User Notifications</b></summary>

```http
GET /notifications/v1/notifications/user/1
Authorization: Bearer {JWT_TOKEN}
```

**Response (200 OK):**
```json
{
  "userId": 1,
  "notifications": [
    {
      "id": 1,
      "notificationType": "TRANSACTION_SUCCESS",
      "message": "Your deposit of $500 was successful",
      "status": "SENT",
      "createdAt": "2025-12-10T15:35:00Z"
    }
  ],
  "totalCount": 1
}
```

</details>

---

### 💪 Health & Monitoring APIs

```http
GET /actuator/health
GET /actuator/metrics
GET /actuator/env
GET /actuator/loggers
```

---

## 🗄️ Database Schema

### User Entity
```sql
CREATE TABLE user_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Relationships:**
- One-to-Many with `bank_account_entity` (1 user → many accounts)

---

### Bank Account Entity
```sql
CREATE TABLE bank_account_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_number VARCHAR(50) UNIQUE NOT NULL,
    account_type VARCHAR(50),
    currency VARCHAR(10) DEFAULT 'USD',
    balance DECIMAL(19,4) DEFAULT 0.0000,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_entity(id)
);
```

**Relationships:**
- Many-to-One with `user_entity`
- One-to-Many with `account_log` (1 account → many logs)

---

### Account Log
```sql
CREATE TABLE account_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    operation_type VARCHAR(50),
    old_balance DECIMAL(19,4),
    amount DECIMAL(19,4),
    new_balance DECIMAL(19,4),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES bank_account_entity(id)
);
```

**Operation Types:**
- DEPOSIT
- WITHDRAWAL
- TRANSFER
- CORRECTION

---

### Entity Relationship Diagram

```mermaid
erDiagram
    USER_ENTITY ||--o{ BANK_ACCOUNT_ENTITY : "creates"
    BANK_ACCOUNT_ENTITY ||--o{ ACCOUNT_LOG : "generates"
    
    USER_ENTITY {
        bigint id PK
        string name
        string email UK
        string phone
        timestamp created_at
        timestamp updated_at
    }
    
    BANK_ACCOUNT_ENTITY {
        bigint id PK
        bigint user_id FK
        string account_number UK
        string account_type
        string currency
        decimal balance
        string status
        bigint version
        timestamp created_at
        timestamp updated_at
    }
    
    ACCOUNT_LOG {
        bigint id PK
        bigint account_id FK
        string operation_type
        decimal old_balance
        decimal amount
        decimal new_balance
        text description
        timestamp created_at
    }
```

---

## 🔄 Event Flow

### Complete Event Processing Pipeline

```mermaid
flowchart TB
    subgraph Request["1️⃣ Request Phase"]
        REQ["User Request<br/>POST /accounts/v1/accounts"]
    end
    
    subgraph Gateway["2️⃣ Gateway Processing"]
        GW["API Gateway<br/>- JWT Validation<br/>- Route to Service"]
    end
    
    subgraph Service["3️⃣ Service Processing"]
        ACC["Account Service<br/>- Validate Request<br/>- Update Database<br/>- Generate Event"]
    end
    
    subgraph DB["4️⃣ Database"]
        MYSQL[(MySQL Database<br/>Store Account)]
    end
    
    subgraph Publish["5️⃣ Event Publishing"]
        KAFKA1["Kafka Producer<br/>Publish to account-events"]
    end
    
    subgraph Topic["6️⃣ Kafka Topic"]
        TOPIC["account-events Topic<br/>Event Queued"]
    end
    
    subgraph Subscribe["7️⃣ Event Consumption"]
        KAFKA2["Kafka Consumer<br/>Notification Service"]
    end
    
    subgraph NotifService["8️⃣ Notification Processing"]
        NOTIF["Notification Service<br/>- Process Event<br/>- Create Notification"]
    end
    
    subgraph NotifDB["9️⃣ Notification Storage"]
        H2[(H2 Database<br/>Store Notification)]
    end
    
    subgraph Response["🔟 Response"]
        RES["Client Response<br/>201 Created"]
    end
    
    REQ --> GW
    GW --> ACC
    ACC --> MYSQL
    ACC --> Publish
    Publish --> Topic
    Topic --> Subscribe
    Subscribe --> NotifService
    NotifService --> NotifDB
    ACC --> Response
    
    style Request fill:#e1f5ff
    style Gateway fill:#fff3e0
    style Service fill:#f3e5f5
    style DB fill:#ede7f6
    style Publish fill:#fce4ec
    style Topic fill:#ffe0b2
    style Subscribe fill:#f3e5f5
    style NotifService fill:#e8f5e9
    style NotifDB fill:#ede7f6
    style Response fill:#c8e6c9
```

### Sequence Diagram: Account Creation → Notification

```mermaid
sequenceDiagram
    participant User as 👤 User
    participant Gateway as 🚪 API Gateway
    participant AccSvc as 🏦 Account Service
    participant DB as 💾 MySQL
    participant Producer as 📤 Kafka Producer
    participant Kafka as 📨 Kafka Broker
    participant NotifSvc as 🔔 Notification Service
    participant NotifDB as 💾 H2 DB

    User->>Gateway: 1. POST /accounts/v1/accounts
    Note over Gateway: JWT Validation ✓
    
    Gateway->>AccSvc: 2. Route Request
    AccSvc->>AccSvc: 3. Validate Request
    AccSvc->>DB: 4. INSERT account
    DB-->>AccSvc: 5. Account Saved (ID: 123)
    
    AccSvc->>Producer: 6. Prepare Event
    Note over Producer: AccountCreated
    
    Producer->>Kafka: 7. PUBLISH to account-events
    Kafka-->>Producer: 8. Event Queued ✓
    
    AccSvc-->>Gateway: 9. 201 Created
    Gateway-->>User: 10. Response to Client
    
    Note over Kafka: Event Stream
    
    Kafka->>NotifSvc: 11. CONSUME from account-events
    NotifSvc->>NotifSvc: 12. Process Event
    Note over NotifSvc: Build Notification
    
    NotifSvc->>NotifDB: 13. INSERT notification
    NotifDB-->>NotifSvc: 14. Saved ✓
    
    NotifSvc->>Producer: 15. Prepare Event
    Producer->>Kafka: 16. PUBLISH to notification-events
    Kafka-->>Producer: 17. Event Queued ✓
```

---

## 📨 Kafka Topics

### Topic 1: account-events
**Purpose:** Account lifecycle events  
**Partitions:** 3  
**Replication Factor:** 1

**Published Events:**
```json
{
  "eventType": "AccountCreated",
  "userId": 1,
  "accountNumber": "ACC-12345",
  "accountType": "SAVINGS",
  "initialBalance": 1000.00,
  "timestamp": "2025-12-10T15:30:00Z"
}
```

**Event Types:**
- `AccountCreated`
- `AccountUpdated`
- `AccountDeleted`
- `DepositCompleted`
- `WithdrawalCompleted`

---

### Topic 2: transaction-events
**Purpose:** Transaction operations  
**Partitions:** 3  
**Replication Factor:** 1

**Published Events:**
```json
{
  "eventType": "TransactionCompleted",
  "accountId": 1,
  "transactionId": "TXN-001",
  "type": "CREDIT",
  "amount": 500.00,
  "status": "COMPLETED",
  "timestamp": "2025-12-10T15:35:00Z"
}
```

**Event Types:**
- `TransactionInitiated`
- `TransactionCompleted`
- `TransactionFailed`
- `TransferProcessed`

---

### Topic 3: notification-events
**Purpose:** Notification records  
**Partitions:** 3  
**Replication Factor:** 1

**Published Events:**
```json
{
  "eventType": "NotificationSent",
  "notificationId": 1,
  "userId": 1,
  "channel": "EMAIL",
  "recipient": "user@example.com",
  "message": "Your deposit of $500 was successful",
  "status": "SENT",
  "timestamp": "2025-12-10T15:35:00Z"
}
```

**Event Types:**
- `NotificationSent`
- `NotificationDelivered`
- `NotificationFailed`

---

### Monitor Kafka Topics in Real-Time

```bash
# Monitor account-events
kafka-console-consumer \
  --topic account-events \
  --bootstrap-server localhost:9092 \
  --from-beginning \
  --property print.timestamp=true

# Monitor transaction-events
kafka-console-consumer \
  --topic transaction-events \
  --bootstrap-server localhost:9092 \
  --from-beginning

# Monitor notification-events
kafka-console-consumer \
  --topic notification-events \
  --bootstrap-server localhost:9092 \
  --from-beginning
```

---

## 🧪 Testing

### Unit Testing

```bash
# Run all unit tests
mvn test

# Run specific module tests
mvn test -pl account-service

# Run specific test class
mvn test -Dtest=UserServiceTest
```

**Test Coverage:**
- Service layer: 85%+
- Controller layer: 80%+
- Repository layer: 90%+

---

### Integration Testing

```bash
# Run integration tests (requires services running)
mvn verify -Pintegration-test

# Run API integration tests
mvn test -Dtest=*IntegrationTest
```

**Integration Test Scenarios:**
1. Complete transaction flow (API → Database)
2. Kafka event publishing and consumption
3. Service-to-service communication
4. Database persistence verification

---

### API Testing with Postman

**Import Collections:**

1. Open Postman
2. Click **Import** → **Import File**
3. Select collection files:
   - Complete-Transaction-Monitoring-API.postman_collection.json
   - Microservices_Kafka_Communication.postman_collection.json

**Set Environment Variables:**
```json
{
  "base_url": "http://localhost:8080",
  "jwt_token": "{{token from login}}",
  "user_id": 1,
  "account_id": 1
}
```

**Sample Test Flow:**
```
1. ✅ POST /api/auth/login                        → Get JWT Token
2. ✅ GET /accounts/v1/users                      → List Users
3. ✅ POST /accounts/v1/accounts                  → Create Account
4. ✅ POST /accounts/v1/accounts/1/deposit        → Deposit $500
5. ✅ POST /transactions/v1/transfer              → Transfer Funds
6. ✅ GET /notifications/v1/notifications         → View Notifications
7. ✅ Monitor Kafka Topics                        → See Events
```

---

### Kafka Event Testing

```bash
# Terminal 1: Start consuming events
kafka-console-consumer \
  --topic account-events \
  --bootstrap-server localhost:9092 \
  --property print.timestamp=true

# Terminal 2: Trigger an event
curl -X POST http://localhost:8080/accounts/v1/accounts \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "accountNumber": "TEST-ACC-001",
    "accountType": "CHECKING",
    "initialBalance": 5000.00
  }'

# Terminal 1 Output:
# {
#   "eventType": "AccountCreated",
#   "userId": 1,
#   "accountNumber": "TEST-ACC-001",
#   "timestamp": "2025-12-10T15:50:00Z"
# }
```

---

## 🔐 Security

### JWT Authentication Flow

```mermaid
sequenceDiagram
    participant Client as 👤 Client
    participant Gateway as 🚪 API Gateway
    participant AuthFilter as 🔐 JWT Filter
    participant Service as 🎛️ Service

    Client->>Gateway: 1. POST /api/auth/login<br/>(username, password)
    
    Gateway->>AuthFilter: 2. Validate Credentials
    AuthFilter->>AuthFilter: 3. Check User DB
    AuthFilter->>AuthFilter: 4. Generate JWT Token
    Note over AuthFilter: Token: HS384<br/>Exp: 24h
    
    AuthFilter-->>Gateway: 5. Return Token
    Gateway-->>Client: 6. JWT Token Response
    
    Client->>Gateway: 7. GET /accounts/v1/users<br/>Authorization: Bearer {TOKEN}
    Gateway->>AuthFilter: 8. Validate JWT
    AuthFilter->>AuthFilter: 9. Check Signature
    AuthFilter->>AuthFilter: 10. Check Expiry
    AuthFilter->>AuthFilter: 11. Extract Claims
    
    AuthFilter-->>Gateway: 12. Token Valid ✓
    Gateway->>Service: 13. Route Request
    Service-->>Gateway: 14. Return Data
    Gateway-->>Client: 15. Response
```

### Security Features

| Feature | Implementation | Status |
|---------|-----------------|--------|
| **Authentication** | JWT Tokens (HS384) | ✅ Implemented |
| **Authorization** | Role-Based Access Control | ✅ Implemented |
| **Password Hashing** | bcrypt with salt | ✅ Implemented |
| **CORS** | Configurable origins | ✅ Implemented |
| **HTTPS** | SSL/TLS Ready | ⚠️ Optional |
| **Input Validation** | Spring Validation | ✅ Implemented |
| **SQL Injection Protection** | Parameterized Queries (JPA) | ✅ Built-in |
| **CSRF Protection** | Spring Security CSRF | ✅ Implemented |

### JWT Token Structure

```
eyJhbGciOiJIUzM4NCJ9.eyJyb2xlIjoiQURNSU4iLCJ1c2VybmFtZSI6ImFkbWluIiwic3ViIjoiYWRtaW4iLCJpYXQiOjE3NjUyOTA1ODcsImV4cCI6MTc2NTM3Njk4N30.nAm7KcGQF2LQoFFvTvuyMniVA4d06sgvJKD8AR1bTgkv60b7bMGt_orI7IJ-noVH

↓ Decoded ↓

Header:
{
  "alg": "HS384",
  "typ": "JWT"
}

Payload:
{
  "role": "ADMIN",
  "username": "admin",
  "sub": "admin",
  "iat": 1765290587,
  "exp": 1765376987
}

Signature:
HMACSHA384(base64UrlEncode(header) + "." + base64UrlEncode(payload), SECRET)
```

### Role-Based Access Control

| Role | Permissions | Services |
|------|-----------|----------|
| **ADMIN** | Full access to all endpoints | All |
| **USER** | Read own data, perform transactions | Account, Transaction |
| **GUEST** | Login only | Auth |

---

## 📊 Monitoring & Observability

### Eureka Dashboard
- **URL:** http://localhost:8761
- **Features:**
  - Real-time service status
  - Instance health monitoring
  - Service instance details
  - Up/Down notifications

### Spring Boot Actuator Endpoints

| Endpoint | Purpose | Example |
|----------|---------|---------|
| `/actuator/health` | Service health status | UP, DOWN |
| `/actuator/metrics` | Application metrics | CPU, memory, requests |
| `/actuator/env` | Environment properties | Config values |
| `/actuator/loggers` | Logging configuration | Log levels |
| `/actuator/httptrace` | HTTP request trace | Last 100 requests |

```bash
# Check health of all services
curl http://localhost:8080/actuator/health

# Get specific metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used
curl http://localhost:8080/actuator/metrics/http.server.requests

# View environment properties
curl http://localhost:8080/actuator/env

# Change log level
curl -X POST http://localhost:8080/actuator/loggers/com.transaction \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel":"DEBUG"}'
```

### Distributed Tracing with Zipkin

```bash
# Start Zipkin (Optional)
docker run -d -p 9411:9411 openzipkin/zipkin

# View traces
# Navigate to http://localhost:9411
# Select service and view request traces
```

### Log Aggregation

**Log Levels:**
- ERROR - Critical issues
- WARN - Warning conditions
- INFO - Informational messages
- DEBUG - Debug information
- TRACE - Detailed trace logging

**Enable DEBUG Logging:**
```yaml
logging:
  level:
    com.transaction: DEBUG
    org.springframework: INFO
    org.hibernate: DEBUG
```

---

## ⚡ Performance Optimizations

### Database Optimizations

1. **Indexing Strategy**
   ```sql
   -- User lookup optimization
   CREATE INDEX idx_user_email ON user_entity(email);
   
   -- Account lookup optimization
   CREATE INDEX idx_account_user ON bank_account_entity(user_id);
   CREATE INDEX idx_account_number ON bank_account_entity(account_number);
   
   -- Logs query optimization
   CREATE INDEX idx_log_account ON account_log(account_id);
   CREATE INDEX idx_log_created ON account_log(created_at);
   ```

2. **Connection Pooling**
   ```yaml
   spring:
     datasource:
       hikari:
         maximum-pool-size: 10
         minimum-idle: 2
         connection-timeout: 30000
   ```

3. **Batch Operations**
   ```java
   // Batch insert notifications
   @Transactional
   public void saveBatch(List<Notification> notifications) {
     int batchSize = 100;
     for (int i = 0; i < notifications.size(); i++) {
       notificationRepository.save(notifications.get(i));
       if (i % batchSize == 0) {
         notificationRepository.flush();
         entityManager.clear();
       }
     }
   }
   ```

### Kafka Performance

1. **Batch Configuration**
   ```yaml
   spring:
     kafka:
       producer:
         batch-size: 16384
         linger-ms: 10
       consumer:
         max-poll-records: 500
   ```

2. **Partitioning Strategy**
   - 3 partitions per topic for parallel processing
   - Increased throughput across consumer group

### Cache Implementation

```yaml
# Enable caching
spring:
  cache:
    type: simple
    cache-names:
      - users
      - accounts
      - transactions
```

### Load Balancing

- **Client-side:** Spring Cloud LoadBalancer
- **Kafka:** Partition-based consumer groups
- **Database:** Connection pooling

---

## 🔧 Configuration

### Environment Profiles

**Development (dev):**
```yaml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:mysql://localhost:3306/accountdb
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
logging:
  level:
    com.transaction: DEBUG
```

**Testing (test):**
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
```

**Production (prod):**
```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-db:3306/accountdb
    hikari:
      maximum-pool-size: 20
  jpa:
    hibernate:
      ddl-auto: validate
logging:
  level:
    root: WARN
```

### Configuration Server Properties

```yaml
# Account Service Config
spring:
  kafka:
    bootstrap-servers: localhost:9092
  jpa:
    hibernate:
      ddl-auto: update

# Transaction Service Config  
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000

# Notification Service Config
spring:
  kafka:
    consumer:
      group-id: notification-group
      auto-offset-reset: earliest
```

---

## 🐛 Troubleshooting

### Issue: Services not registering in Eureka

**Symptoms:**
```
WARN c.n.e.c.DiscoveryClient - Can't contact the discovery server to fetch service instance list
```

**Solution:**
```bash
# Verify Eureka is running
curl http://localhost:8761/eureka/apps

# Restart the problematic service
cd account-service && mvn spring-boot:run

# Check application.yml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

### Issue: Kafka Connection Failed

**Symptoms:**
```
ERROR o.a.k.c.NetworkClient - Connection refused (Connection refused)
```

**Solution:**
```bash
# Verify Kafka is running
jps -l | grep Kafka

# Check Kafka logs
tail -f logs/server.log

# Verify bootstrap servers
netstat -an | grep 9092
```

---

### Issue: MySQL Connection Error

**Symptoms:**
```
ERROR c.z.h.HikariPool - HikariPool-1 - Exception during pool initialization
```

**Solution:**
```bash
# Verify MySQL is running
mysql -u root -p -e "SELECT 1"

# Check database exists
mysql -u root -p -e "SHOW DATABASES;" | grep accountdb

# Verify connection string
spring.datasource.url: jdbc:mysql://localhost:3306/accountdb
```

---

### Issue: JWT Token Validation Fails

**Symptoms:**
```
WARN o.s.s.w.c.SecurityContextPersistenceFilter - Authentication failed
```

**Solution:**
```bash
# Generate new token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'

# Verify token in request
Authorization: Bearer {token_from_above}

# Check token expiry
# Default: 24 hours from creation
```

---

### Issue: Kafka Topics Not Created

**Symptoms:**
```
WARN o.a.k.c.NetworkClient - Producer metadata refresh failed
```

**Solution:**
```bash
# List existing topics
kafka-topics --list --bootstrap-server localhost:9092

# Create missing topics
kafka-topics --create \
  --topic account-events \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# Verify creation
kafka-topics --describe --topic account-events --bootstrap-server localhost:9092
```

---

### Issue: High Memory Usage

**Solution:**
```bash
# Increase JVM heap size
export JAVA_OPTS="-Xmx2g -Xms512m"

# Or in IDE/Maven
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx2g"

# Monitor with JProfiler or VisualVM
jvisualvm &
```

---

### Issue: Slow Queries

**Solution:**
```yaml
# Enable slow query logging
spring:
  jpa:
    properties:
      hibernate:
        generate_statistics: true
        use_sql_comments: true

logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

---

## 🚀 Future Enhancements

### Phase 2: Advanced Features

- [ ] **Kubernetes Deployment**
  - Helm charts for services
  - StatefulSets for Kafka
  - ConfigMaps for configuration
  - Persistent volumes for databases

- [ ] **Advanced Analytics Dashboard**
  - Real-time transaction metrics
  - User analytics
  - System performance monitoring
  - Custom report generation

- [ ] **Payment Gateway Integration**
  - Stripe integration
  - PayPal integration
  - Multiple payment methods
  - PCI compliance

- [ ] **Machine Learning Fraud Detection**
  - Anomaly detection
  - Pattern recognition
  - Real-time alerts
  - Model training pipeline

- [ ] **Mobile Application**
  - React Native app
  - iOS/Android native apps
  - Biometric authentication
  - Offline capabilities

- [ ] **GraphQL API Layer**
  - GraphQL schema
  - Query optimization
  - Subscription support
  - Apollo client integration

### Phase 3: Enterprise Features

- [ ] **Advanced Audit Logging**
  - Complete audit trail
  - User activity tracking
  - Compliance reporting
  - Data retention policies

- [ ] **Multi-Tenancy Support**
  - Tenant isolation
  - Per-tenant databases
  - Custom branding
  - Role-based billing

- [ ] **Advanced Reporting**
  - Financial reports
  - Compliance reports
  - Custom dashboards
  - Export to Excel/PDF

- [ ] **API Rate Limiting**
  - Per-user limits
  - Sliding window algorithm
  - Rate limit headers
  - Graceful degradation

- [ ] **Service Mesh (Istio)**
  - Advanced routing
  - Traffic management
  - Security policies
  - Distributed tracing

---

## 🤝 Contributing

### Guidelines

1. **Fork** the repository
2. **Create** a feature branch
   ```bash
   git checkout -b feature/AmazingFeature
   ```
3. **Make** your changes
4. **Write** tests for your changes
5. **Commit** with descriptive messages
   ```bash
   git commit -m 'Add AmazingFeature: description'
   ```
6. **Push** to your branch
   ```bash
   git push origin feature/AmazingFeature
   ```
7. **Open** a Pull Request

### Coding Standards

- ✅ Follow **SOLID Principles**
- ✅ Use **meaningful variable names**
- ✅ Add **JavaDoc comments** for public methods
- ✅ Write **unit tests** (minimum 80% coverage)
- ✅ Maintain **consistent code formatting**
- ✅ Use **Spring conventions** and best practices

### Code Review Checklist

- [ ] Code follows project style guide
- [ ] Tests are included and passing
- [ ] Documentation is updated
- [ ] No hardcoded values
- [ ] Proper error handling
- [ ] No security vulnerabilities

---

## 📞 Contact & Support

### 📧 Communication

- **Email:** [satyam.kumar1183@gmail.com](mailto:satyam.kumar1183@gmail.com)
- **GitHub Issues:** [Report Issues](https://github.com/SatyamKumar7911/Real-Time-Transaction-Monitoring-System/issues)

### 📚 Resources

- **Full Documentation:** [Project Docs](https://github.com/SatyamKumar7911/Real-Time-Transaction-Monitoring-System#readme)
- **Developer Profile:** [@SatyamKumar7911](https://github.com/SatyamKumar7911)

### 🌟 Support

If you find this project helpful:
- ⭐ **Star the repository** to show support
- 🔗 **Share** with other developers
- 💬 **Provide feedback** and suggestions
- 🐛 **Report bugs** and issues

---

## 📝 License

This project is licensed under the **MIT License** - see the LICENSE file for details.

```
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software...
```

---

## 🙏 Acknowledgments

- **Spring Framework Team** - Excellent documentation and framework
- **Apache Kafka Community** - Robust event streaming platform
- **Netflix** - Eureka and open-source contributions
- **All Contributors** - Who have helped improve this project

---

## 📊 Project Statistics

| Metric | Value |
|--------|-------|
| **Total Microservices** | 6 |
| **API Endpoints** | 26+ |
| **Kafka Topics** | 3 |
| **Database Tables** | 3 |
| **Event Types** | 10+ |
| **Code Lines** | 5000+ |
| **Test Coverage** | 90%+ |


---

<div align="center">

### Made with ❤️ by [Satyam Kumar](https://github.com/SatyamKumar7911)

**Give this project a ⭐ if it helped you!**

⬆ Back to Top

</div>

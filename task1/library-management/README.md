# 📚 Library Management System

A full-featured Library Management System built with **Spring Boot**, **Spring Security**, **Spring Data JPA**, **Thymeleaf**, and **Lombok** — with **no REST APIs** (server-side MVC only).

---

## 🚀 Tech Stack

| Layer         | Technology                          |
|---------------|-------------------------------------|
| Framework     | Spring Boot 3.2                     |
| Security      | Spring Security 6 (Form Login)      |
| Persistence   | Spring Data JPA + Hibernate         |
| Database      | H2 (in-memory, dev)                 |
| Templating    | Thymeleaf + Thymeleaf Security Extras|
| Utilities     | Lombok                              |
| Build         | Maven                               |

---

## 🗂️ Project Structure

```
src/main/java/com/library/
├── LibraryManagementApplication.java   ← Main entry point
├── config/
│   ├── SecurityConfig.java             ← Spring Security setup
│   └── DataInitializer.java            ← Seeds demo data on startup
├── entity/
│   ├── Role.java
│   ├── User.java
│   ├── Author.java
│   ├── Book.java
│   └── IssueRecord.java
├── repository/
│   ├── RoleRepository.java
│   ├── UserRepository.java
│   ├── AuthorRepository.java
│   ├── BookRepository.java
│   └── IssueRecordRepository.java
├── service/
│   ├── CustomUserDetailsService.java
│   ├── UserService.java
│   ├── AuthorService.java
│   ├── BookService.java
│   └── IssueRecordService.java
└── controller/
    ├── AuthController.java             ← /login, /register
    ├── AdminController.java            ← /admin/**
    ├── LibrarianController.java        ← /librarian/**
    └── MemberController.java           ← /member/**

src/main/resources/
├── application.properties
├── templates/
│   ├── auth/    login.html, register.html
│   ├── admin/   dashboard, users, authors, books, issues + forms
│   ├── librarian/ dashboard, books, issue-form, return-form, issues, overdue, members, member-history
│   └── member/  dashboard, books, my-books
└── static/
    ├── css/style.css
    └── js/app.js
```

---

## 🔐 Roles & Access

| Role       | Access                                               |
|------------|------------------------------------------------------|
| ADMIN      | Full access: users, authors, books, all issue records |
| LIBRARIAN  | Manage books, issue/return, view overdue & members   |
| MEMBER     | Browse catalog, view own borrowing history           |

---

## 🎯 Demo Credentials

| Role       | Email                      | Password    |
|------------|----------------------------|-------------|
| Admin      | admin@library.com          | admin123    |
| Librarian  | librarian@library.com      | lib123      |
| Member     | member@library.com         | member123   |

---

## ⚙️ How to Run

### Prerequisites
- Java 17+
- Maven 3.8+

### Steps

```bash
# 1. Navigate to project root
cd library-management

# 2. Build the project
mvn clean install

# 3. Run the application
mvn spring-boot:run
```

Open your browser at: **http://localhost:8080**

### H2 Console (dev only)
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:librarydb`
- Username: `sa` | Password: *(empty)*

---

## 📋 Features

### Admin
- Dashboard with stats (users, books, authors, active issues)
- Create / enable / disable / delete users with role assignment
- Full CRUD for Authors
- Full CRUD for Books (with available copy tracking)
- View all issue records

### Librarian
- Dashboard with live stats
- Search book catalog
- Issue a book to a member (14-day loan, max 3 books/member)
- Process book returns
- View & manage overdue books
- View member list & individual borrowing history

### Member
- Personal dashboard showing currently borrowed books
- Browse full book catalog with search
- See availability status per book
- View complete personal borrowing history

---

## 🏗️ Business Rules

- **Loan period**: 14 days
- **Max concurrent issues per member**: 3
- **Overdue detection**: automatic on each overdue/dashboard page load
- **Available copies**: auto-decremented on issue, incremented on return
- **Passwords**: BCrypt-hashed

---

## 📦 Building a JAR

```bash
mvn clean package
java -jar target/library-management-1.0.0.jar
```

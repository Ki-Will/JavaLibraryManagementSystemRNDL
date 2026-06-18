
# Rwanda Digital Library Management System

A Java-based library management system with object-oriented design, multi-threading support, and JDBC persistence for PostgreSQL.

---

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Set Up Database](#set-up-database)
  - [Build and Run](#build-and-run)
- [Usage Guide](#usage-guide)
  - [ConsoleMain: Interactive CLI](#consolemain-interactive-cli)
  - [Main: Quick Test Application](#main-quick-test-application)
- [Persistence with JDBC](#persistence-with-jdbc)

---

## Features

- **Object-Oriented Design**: Encapsulated model classes for Book and Member
- **Java Collections**: Manage books and members with HashMap and ArrayList
- **Multi-Threading**:
  - Simultaneous borrowing operations
  - Thread-safe access with ReentrantLock
  - ExecutorService for managing librarian threads
- **Borrowing Rules**: Members can borrow a maximum of 5 books
- **JDBC Persistence**: Connects to PostgreSQL for storing books, members, and borrowing records
- **Interactive CLI**: ConsoleMain for managing the library via the terminal
- **Docker Support**: One-click PostgreSQL database setup with Docker Compose - Strictly for starting the PostgreSQL database container

---

## Tech Stack

- Java 17
- Maven
- PostgreSQL 17 (Alpine image for Docker)
- JDBC (PostgreSQL driver)
- Docker & Docker Compose

---

## Project Structure

```
/
├── README.md                           # This file
├── pom.xml                             # Maven project configuration with Postgres JDBC dependency
├── docker-compose.yml                  # PostgreSQL Docker setup
├── library_schema.sql                  # Database schema and sample data
└── src/
    └── main/
        └── java/
            ├── model/
            │   ├── Book.java           # Book entity class (ISBN, title, author, publication year, availability)
            │   └── Member.java         # Member entity class (ID, name, list of borrowed books)
            ├── service/
            │   ├── LibraryService.java # Core service for managing library operations, thread-safe
            │   └── BorrowTask.java     # Runnable task for concurrent borrowing operations
            ├── dao/
            │   └── LibraryDAO.java     # Data Access Object for database operations
            ├── util/
            │   └── DatabaseUtil.java   # Helper class for JDBC connection management
            ├── Main.java               # Quick test application to demonstrate core functionality
            └── ConsoleMain.java        # Interactive CLI for librarians
```

---

## Getting Started

### Prerequisites
1. **Java 17+**: Ensure Java is installed and on your system PATH
2. **Maven**: For dependency management and building
3. **Docker & Docker Compose**: To run PostgreSQL database

### Set Up Database

#### Option 1: Use Docker Compose (Recommended)
1. Open PowerShell in the project root directory
2. Start the PostgreSQL container:
   ```powershell
   docker-compose up -d
   ```
   This will:
   - Pull the `postgres:17-alpine` Docker image (if not already present)
   - Start a container named `library-db`
   - Automatically initialize the database using `library_schema.sql`
   - Expose PostgreSQL on port `5432`
   - Set up username: `library_user`, password: `library_password`, database: `library_db`

#### Option 2: Manual PostgreSQL Setup
If you already have PostgreSQL running locally:
1. Create a database named `library_db`
2. Execute the `library_schema.sql` script to create tables and insert sample data
3. Update `DatabaseUtil.java` with your custom database credentials

### Build and Run

First, let's clean and compile the project to ensure all dependencies are downloaded:

```powershell
mvn clean compile
```

Now you're ready to run one of the two main applications!

---

## Usage Guide

### 1. ConsoleMain: Interactive CLI for Librarians

This is the main interactive application designed for librarians to manage the library system directly from the terminal.

**What it Does**:
- Provides a user-friendly, menu-driven interface
- Allows full CRUD operations for books and members (create, read, update, delete)
- Handles borrowing/returning logic
- Includes a concurrent borrowing simulation tool

**What it Utilizes**:
- `LibraryService` (core service class with in-memory collections)
- `Book` and `Member` model classes
- `BorrowTask` and `ExecutorService` for concurrent operations
- `ReentrantLock` to ensure thread-safe access to shared data
- `System.in` and `System.out` for interactive user input/output

**Run Command**:
```powershell
mvn exec:java "-Dexec.mainClass=ConsoleMain"
```

**Menu Options**:
- **1 - List All Books**: Shows every book in the library (including borrowed)
- **2 - List Available Books**: Shows only books that are currently available to borrow
- **3 - Add Book**: Add a new book to the library (enter ISBN, title, author, and year)
- **4 - List All Members**: Shows every registered member and their borrowed books
- **5 - Add Member**: Add a new library member (enter ID and name)
- **6 - Borrow Book**: Allow a member to borrow a book (checks member ID, ISBN, availability, and max 5 books)
- **7 - Return Book**: Allow a member to return a book
- **8 - Simulate Concurrent Borrowing**: Run a multi-threaded test where multiple librarians try to borrow books at the same time
- **0 - Exit**: Close the application

---

### 2. Main: Quick Test Application

This is a simple, non-interactive test application that quickly demonstrates the core functionality of the system without requiring user input.

**What it Does**:
- Initializes library with 4 sample books and 3 sample members
- Displays initial list of available books
- Spawns multiple concurrent librarian threads to test simultaneous borrowing
- Shows results of the concurrent test (which members borrowed which books, available books after test)
- Demonstrates returning a book and checks updated availability

**What it Utilizes**:
- `LibraryService` (core service class with in-memory collections)
- `Book` and `Member` model classes
- `BorrowTask` class to represent each concurrent borrow operation
- `ExecutorService` to manage and run a pool of threads
- `ReentrantLock` to ensure thread-safe access to the library state
- Direct `System.out.println()` for displaying test output

**Run Command**:
```powershell
mvn exec:java "-Dexec.mainClass=Main"
```

### Stopping the Database
When you're done using the application, you can stop the Docker container:
```powershell
docker-compose down
# or using the script: .\stop-db.ps1
```

---

## Persistence with JDBC

The system includes a `LibraryDAO.java` and `DatabaseUtil.java` for interacting with the PostgreSQL database:
- `recordBorrow()`: Records a borrowing transaction and marks a book as unavailable
- `recordReturn()`: Updates the database when a book is returned and marks it as available
- Proper exception handling and resource management
- Auto-commit disabled to ensure atomicity for borrow/return operations
- Rollback on any SQL exception

Note: The core `LibraryService` currently uses in-memory collections for thread-safe testing purposes. To use full database persistence, you can extend `LibraryService` to use `LibraryDAO`.

---

## Notes

- To package the application into a JAR file, run:
  ```powershell
  mvn clean package
  ```
- The JAR file will be in the `target/` directory
- For production use, consider implementing connection pooling and additional error handling


# AuthZ Service Simulator (Day 8)

A console-based Java program that extends authentication with **role-based authorization**.  
Simulates user registration, login, logout, and role checks (e.g., ADMIN-only delete user).

## Concepts Covered
- Authentication vs Authorization
- Role-Based Access Control (RBAC)
- Session token validation
- File persistence for users
- CLI-based restricted commands

## How to Run
```bash
javac -d out src/dev/ayush/authzServiceSimulator/**/*.java
java -cp out dev.ayush.authzServiceSimulator.AuthzSimulatorApp
# Moko Games

**DDD-driven microservices platform for buying, playing and socializing around digital board games.**

Moko Games is a course project developed during **Integration Project 3**. We designed and implemented a **Domain-Driven Design (DDD) microservices platform** where users can purchase digital board games, play with friends (or AI), and use social and chat features in one unified system.

---

## One-line summary

Moko is a unified platform to buy, play, and socialize around digital board games — built as a DDD microservices project.

---

## Tech Stack

* Java 21
* Spring Boot
* Domain-Driven Design (DDD)
* Microservices architecture
* RabbitMQ
* PostgreSQL
* Keycloak
* React
* Docker
* Kubernetes
* WebSockets

---

## Repository Structure

```
moko-games/
│
├── platform/               # Platform microservices & frontend
│   ├── platform-frontend
│   ├── user-service
│   ├── session-service
│   ├── communication-service
│   ├── social-service
│   ├── store-service
│   └── games-service
│
├── games/                  # Game services & frontends
│   ├── tic-tac-toe-service
│   ├── tic-tac-toe-frontend
│   ├── checkers-service
│   └── checkers-frontend
│
├── shared/                 # Cross-cutting services
│   ├── game-acl-service
│   └── socket-service
│
└── docs/                   # Project documentation
```

---

## Core Features

* Store & library management for digital board games
* User profiles, friends, and social interactions
* Real-time lobby, chat, and notifications
* Multiplayer gameplay and AI opponents
* AI chatbot integration

---

## Architecture Highlights

* Clear bounded contexts per domain (store, profiles, lobby, games)
* Event-driven communication using RabbitMQ
* Centralized authentication and authorization with Keycloak
* WebSocket gateway for real-time messaging
* Dockerized services with Kubernetes-based deployment

---

## Local Setup (IntelliJ IDEA)

This repository is intended mainly for **code review and architecture demonstration**. Running the full platform locally requires infrastructure components (Keycloak, RabbitMQ, PostgreSQL) and private container images.

### Backend services

1. Open IntelliJ IDEA
2. Choose **Open** and select the root folder of a specific service (for example `platform/user-service`)
3. Make sure Java **21** is configured as the project SDK
4. Run the main Spring Boot application class

Repeat this for any service you want to inspect or run individually.

### Frontend applications

1. Open the frontend folder (for example `platform/platform-frontend`) in your editor
2. Install dependencies:

   ```bash
   npm install
   ```
3. Start the development server:

   ```bash
   npm run dev
   ```

---

## Testing

* Extensive unit and integration testing across services
* Approximately 90% test coverage
* Focus on domain logic and service boundaries

---

## Team & Roles

Developed by **Team 22**

* Lee — [https://leeco.dev/](https://leeco.dev/)
* Kaj — [https://niceduck.dev/](https://niceduck.dev/)
* Matti — Backend developer

Role: **Fullstack Developer / Architect**

DevOps support: Kevin

---

## Project Context

This project was built as part of **Integration Project 3 (2025)** within a cross-discipline team setup involving application development, AI, and DevOps students.

The original system was developed as multiple independent repositories and later consolidated into this monorepo for demonstration and evaluation purposes.

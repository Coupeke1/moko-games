# Integratiedocumentatie

# Social-service (Friends)

## 1) Overzicht

De **Social-service** verzorgt de **friendship/friends** functionaliteit van het platform:

- Vriendenlijst ophalen
- Inkomende/uitgaande friend requests beheren
- Friend request versturen, accepteren, weigeren, annuleren
- Vriend verwijderen
- Publiceren van **social events** (RabbitMQ) bij belangrijke friend-acties

> Authenticatie: alle `/api/friends/**` endpoints gebruiken `@AuthenticationPrincipal Jwt token` en halen `UserId` uit de JWT. Volgens `SecurityConfig` is **alle verkeer authenticated**, behalve `/actuator/health/**`.

---

## 2) REST API

**Controller:** `FriendsController`  \
**Base path:** `/api/friends`

### 2.1 Vriendenlijst

#### GET `/api/friends`

**Doel:** Haal de huidige vrienden van de ingelogde gebruiker op.

- **Auth:** vereist (JWT)
- **Response:** `200 OK` met `FriendModel[]`

---

### 2.2 Friend requests

#### GET `/api/friends/requests/incoming`

**Doel:** Haal inkomende friend requests op voor de ingelogde gebruiker.

- **Auth:** vereist (JWT)
- **Response:** `200 OK` met `FriendModel[]`

#### GET `/api/friends/requests/outgoing`

**Doel:** Haal uitgaande friend requests op (requests die jij verstuurd hebt en nog pending zijn).

- **Auth:** vereist (JWT)
- **Response:** `200 OK` met `FriendModel[]`

---

### 2.3 Friend request versturen

#### POST `/api/friends`

**Doel:** Verstuur een friend request naar een gebruiker op basis van username.

- **Auth:** vereist (JWT)
- **Request body:** `AddFriendRequestModel`
- **Response:** (void) → typisch `200 OK` of `204 No Content` afhankelijk van Spring-config; controller returnt geen `ResponseEntity`.

**Request**

```json
{ "username": "otherPlayer" }
```

> Opmerking: `username` wordt omgezet naar `new Username(request.username())`. Zorg dat je exact de platform-username gebruikt.

---

### 2.4 Request accepteren / weigeren / annuleren

Alle onderstaande endpoints werken met een `id` in de URL die geïnterpreteerd wordt als **UUID van de andere gebruiker** (`UserId.from(id)`).

#### POST `/api/friends/accept/{id}`

**Doel:** Accepteer een inkomende friend request van user `{id}`.

- **Auth:** vereist (JWT)
- **Path param:** `id: UUID` (= other user id)
- **Response:** void

#### POST `/api/friends/reject/{id}`

**Doel:** Weiger een inkomende friend request van user `{id}`.

- **Auth:** vereist (JWT)
- **Path param:** `id: UUID`
- **Response:** void

#### POST `/api/friends/cancel/{id}`

**Doel:** Annuleer een **uitgaande** friend request naar user `{id}`.

- **Auth:** vereist (JWT)
- **Path param:** `id: UUID`
- **Response:** void

---

### 2.5 Friend verwijderen

#### DELETE `/api/friends/remove/{id}`

**Doel:** Verwijder een bestaande friend relationship met user `{id}`.

- **Auth:** vereist (JWT)
- **Path param:** `id: UUID`
- **Response:** void

---

## 3) Models

### 3.1 Requests

#### `AddFriendRequestModel`

```json
{ "username": "string" }
```

### 3.2 Responses

#### `FriendModel`

```json
{
  "id": "uuid",
  "username": "string",
  "image": "string",
  "statistics": {
    "level": 10,
    "playTime": 12345
  },
  "status": "FRIEND|INCOMING|OUTGOING|..."
}
```

#### `StatisticsModel`

```json
{ "level": 10, "playTime": 12345 }
```

## 4) Messaging (RabbitMQ)

### 4.1 Topology

- **Exchange:** `exchange.social` (topic exchange, durable)
- JSON serialization via `Jackson2JsonMessageConverter`

### 4.2 Uitgaande events

Events worden gepubliceerd door `SocialEventPublisher` via `RabbitTemplate.convertAndSend(exchange, routingKey, event)`.

#### Event: `FriendRequestReceivedEvent`

- **Routing key:** `social.friend-request.received`
- **Wanneer:** wanneer een friend request succesvol verstuurd is.
- **Payload**

```json
{
  "senderId": "uuid",
  "senderName": "string",
  "targetUserId": "uuid"
}
```

#### Event: `FriendRequestAcceptedEvent`

- **Routing key:** `social.friend-request.accepted`
- **Wanneer:** wanneer een friend request geaccepteerd is.
- **Payload**

```json
{
  "senderId": "uuid",
  "senderName": "string",
  "targetUserId": "uuid"
}
```

### 4.3 Contract-afspraken voor consumers

- Berichten zijn JSON.
- Voeg nieuwe velden **backwards compatible** toe (consumers negeren onbekende velden).
- Gebruik routing keys als contract: consumers luisteren op de specifieke keys of een wildcard (bv. `social.friend-request.*`).

---

## 5) Security

### 5.1 CORS

- Allowed origins via `cors.allowed-origins`.
- Methods: `GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD`
- Headers: `*`
- Credentials: toegestaan

### 5.2 Authorization rules (zoals geconfigureerd)

- `/actuator/health/**` → permitAll
- `anyRequest()` → authenticated

**Gevolgen voor integratie**

- Alle friends endpoints vereisen `Authorization: Bearer <jwt>`.

---

## 6) Error handling & HTTP status codes

Er is een globale `@ControllerAdvice` (`ExceptionController`). Responses zijn **plain-text**.

### 404 NOT FOUND

- `FriendshipNotFoundException`
- `NoMatchingUsersException`
- `UserNotFoundException`

### 500 INTERNAL SERVER ERROR

- Alle andere exceptions: `Internal server error: <message>`

---

## 7) Integratie-notities voor externe game developers

### 7.1 Friends UI/flow (frontend)

Een typische frontend flow:

1. Toon vriendenlijst via `GET /api/friends`.
2. Toon requests via:
   - `GET /api/friends/requests/incoming`
   - `GET /api/friends/requests/outgoing`
3. User zoekt op username en verstuurt request via `POST /api/friends`.
4. Ontvanger accepteert/weigert via `POST /api/friends/accept/{userId}` of `POST /api/friends/reject/{userId}`.
5. Sender kan annuleren via `POST /api/friends/cancel/{userId}`.
6. Vriend verwijderen via `DELETE /api/friends/remove/{userId}`.

### 7.2 Events gebruiken voor notificaties/real-time updates

Andere services (bv. Communication-service / Notification-service / Websocket gateway) kunnen subscriben op:

- `social.friend-request.received`
- `social.friend-request.accepted`

Gebruik dit om:

- push notificaties te versturen
- realtime UI updates te triggeren

### 7.3 ACL (anti-corruption layer)

Voor bestaande systemen die al een eigen ‘friend’ concept hadden kan een ACL nodig zijn om:

- platform `UserId` (UUID) te mappen naar legacy identifiers
- username lookup en status semantics gelijk te trekken

Voor nieuwe integraties volstaat het om de REST endpoints te gebruiken en (optioneel) te luisteren naar de RabbitMQ events volgens bovenstaand contract.


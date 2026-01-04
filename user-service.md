# Integratiedocumentatie

# User-service (Profiles, Library, Achievements)

## 1) Overzicht

De **User-service** beheert user-gebonden data en functionaliteiten:

- **Profiles**: profielgegevens (username, email, description, image) + preferences (modules, notifications)
- **Library**: games die een gebruiker bezit + favouriten (en filteren)
- **Achievements**: achievements van een gebruiker, verrijkt met game/achievement details
- **Messaging**: consumeert gameplay/store events en publiceert achievement events

Belangrijke integratiepunten:

- Na aankoop (store) → library wordt geüpdatet (consumer op `GamesPurchasedEvent`).
- Tijdens gameplay → achievements worden toegekend (consumer op `GameAchievementEvent`).
- Bij unlock van achievement → user-service publiceert `AchievementUnlockedEvent` (voor notifications/social).

---

## 2) REST API

### 2.1 Achievements

**Controller:** `AchievementController`  \
**Base path:** `/api/achievements`

#### GET `/api/achievements/me`

**Doel:** Haal achievements op van de ingelogde gebruiker.

- **Auth:** vereist (JWT) — haalt `ProfileId` uit de token
- **Response:** `200 OK` met `AchievementListModel`

#### GET `/api/achievements/{userId}`

**Doel:** Haal achievements op van een specifieke gebruiker.

- **Auth:** permitAll volgens `SecurityConfig` (zie sectie Security)
- **Path param:** `userId: UUID`
- **Response:** `200 OK` met `AchievementListModel`

---

### 2.2 Library

**Controller:** `LibraryController`  \
**Base path:** `/api/library`

#### GET `/api/library/me`

**Doel:** Haal de library van de ingelogde gebruiker op, met optionele filtering.

- **Auth:** vereist (JWT)
- **Query params (optioneel):**
  - `query: string` (vrije tekst)
  - `favourite: boolean` (filter op favourites)
- **Response:** `200 OK` met `LibraryGameModel[]`

**Response (voorbeeld)**

```json
[
  {
    "id": "2f02b1d8-2b7d-4c2f-9b2b-9d9cbe2f4b1b",
    "title": "My Game",
    "description": "…",
    "price": 9.99,
    "image": "https://…",
    "purchasedAt": "2026-01-01T10:00:00Z",
    "favourite": true,
    "healthy": true
  }
]
```

#### GET `/api/library/{id}`

**Doel:** Check of ingelogde gebruiker game `{id}` bezit.

- **Auth:** vereist (JWT)
- **Path param:** `id: UUID` (= `gameId`)
- **Response:** `200 OK` met `true|false`

#### GET `/api/library/{id}/favourite`

**Doel:** Check of game `{id}` gemarkeerd is als favourite.

- **Auth:** vereist (JWT)
- **Path param:** `id: UUID`
- **Response:** `200 OK` met `true|false`

#### PATCH `/api/library/{id}/favourite`

**Doel:** Markeer game `{id}` als favourite.

- **Auth:** vereist (JWT)
- **Path param:** `id: UUID`
- **Response:** `204 No Content`

#### PATCH `/api/library/{id}/unfavourite`

**Doel:** Verwijder favourite-markering voor game `{id}`.

- **Auth:** vereist (JWT)
- **Path param:** `id: UUID`
- **Response:** `204 No Content`

---

### 2.3 Profiles

**Controller:** `ProfileController`  \
**Base path:** `/api/profiles`

#### GET `/api/profiles/me`

**Doel:** Haal eigen profiel op (of maak het aan bij eerste login).

- **Auth:** vereist (JWT)
- **Response:** `200 OK` met `ProfileModel`

#### POST `/api/profiles/bot`

**Doel:** Maak een bot-profiel aan.

- **Auth:** permitAll volgens `SecurityConfig`
- **Response:** `201 Created` met `ProfileModel`

> Typisch gebruikt voor bot/speler-AI accounts.

---

### 2.4 Profile preferences

#### GET `/api/profiles/me/preferences/notifications`

**Doel:** Haal eigen notification preferences op.

- **Auth:** vereist (JWT)
- **Response:** `200 OK` met `NotificationsModel`

#### PATCH `/api/profiles/me/preferences/description`

**Doel:** Update eigen profielbeschrijving.

- **Auth:** vereist (JWT)
- **Request body:** **plain string**
- **Response:** `200 OK` met **plain string** (= nieuwe description)

**Request (voorbeeld)**

```json
"Hello, I love the moko platform"
```

#### PATCH `/api/profiles/me/preferences/image`

**Doel:** Update eigen profielafbeelding.

- **Auth:** vereist (JWT)
- **Request body:** **plain string** (bv. URL)
- **Response:** `200 OK` met **plain string** (= nieuwe image)

#### PATCH `/api/profiles/me/preferences/modules`

**Doel:** Update welke modules zichtbaar/geactiveerd zijn op het profiel.

- **Auth:** vereist (JWT)
- **Request body:** `EditModulesModel`
- **Response:** `200 OK` met `EditModulesModel`

#### PATCH `/api/profiles/me/preferences/notifications`

**Doel:** Update notification preferences.

- **Auth:** vereist (JWT)
- **Request body:** `NotificationsModel`
- **Response:** `200 OK` met `NotificationsModel`

---

### 2.5 Andere profielen opzoeken

#### GET `/api/profiles/{id}`

**Doel:** Haal profiel op via id, inclusief preferences-based filtering.

- **Auth:** method ontvangt een `Jwt token` maar gebruikt die niet in de controller; token kan dus null zijn als endpoint publiek is.
- **Path param:** `id: UUID`
- **Response:** `200 OK` met `FilteredProfileModel`

#### GET `/api/profiles/find/{username}`

**Doel:** Haal profiel op via username, inclusief preferences-based filtering.

- **Auth:** permitAll
- **Path param:** `username: string`
- **Response:** `200 OK` met `FilteredProfileModel`

#### GET `/api/profiles/{id}/preferences/notifications`

**Doel:** Haal notification preferences van gebruiker `{id}` op.

- **Auth:** permitAll
- **Path param:** `id: UUID`
- **Response:** `200 OK` met `NotificationsModel`

---

## 3) Models

### 3.1 Achievements

#### `AchievementListModel`

```json
{ "achievements": [ /* AchievementModel */ ] }
```

#### `AchievementModel`

```json
{
  "gameId": "uuid",
  "code": "string",
  "name": "string",
  "description": "string",
  "level": 1,
  "unlockedAt": "2026-01-01T10:00:00Z",
  "gameName": "string",
  "gameImage": "string"
}
```

> Achievement info wordt verrijkt met details uit de games-service (zie `AchievementDetailsResponse` en `GameDetailsResponse`). Als game-details niet beschikbaar zijn, kunnen `gameName` en `gameImage` `null` zijn.

---

### 3.2 Library

#### `LibraryGameModel`

```json
{
  "id": "uuid",
  "title": "string",
  "description": "string",
  "price": 9.99,
  "image": "string",
  "purchasedAt": "2026-01-01T10:00:00Z",
  "favourite": false,
  "healthy": true
}
```

#### `LibraryGamesModel`

Wordt intern gebruikt als wrapper en bevat:

```json
{ "games": [ /* LibraryGameModel */ ] }
```

---

### 3.3 Profiles

#### `ProfileModel`

```json
{
  "id": "uuid",
  "username": "string",
  "email": "string",
  "description": "string",
  "image": "string",
  "statistics": { "level": 10, "playTime": 12345 },
  "modules": { "achievements": true, "favourites": true },
  "notifications": {
    "receiveEmail": true,
    "social": true,
    "achievements": true,
    "commerce": true,
    "chat": true
  },
  "createdAt": "2026-01-01T10:00:00Z"
}
```

#### `FilteredProfileModel`

```json
{
  "id": "uuid",
  "username": "string",
  "email": "string",
  "description": "string",
  "image": "string",
  "statistics": { "level": 10, "playTime": 12345 },
  "achievements": [ /* AchievementModel */ ],
  "favourites": [
    { "id": "uuid", "title": "string", "description": "string", "image": "string" }
  ]
}
```

> In de service-layer wordt dit “filtered” op basis van voorkeuren (modules): achievements en favourites kunnen leeg/afwezig zijn afhankelijk van de instelling.

#### `EditModulesModel`

```json
{ "achievements": true, "favourites": true }
```

#### `NotificationsModel`

```json
{
  "receiveEmail": true,
  "social": true,
  "achievements": true,
  "commerce": true,
  "chat": true
}
```

#### `StatisticsModel`

```json
{ "level": 10, "playTime": 12345 }
```

---

## 4) Messaging (RabbitMQ)

### 4.1 RabbitMQ config

- Message converter: `Jackson2JsonMessageConverter`
- Consumers via `@RabbitListener` met JSON deserialisatie

### 4.2 Topology

**Exchanges/queues (user-service):**

- `exchange.gameplay` (topic) — inbound gameplay events
- `queue.user.gameplay` — consumer queue voor gameplay achievements
  - binding: routing pattern `game.#.achievement`
- `queue.user-service.games-purchased` — consumer queue voor store purchases
  - **binding niet getoond** in snippet: zorg dat deze queue gebind wordt op het juiste exchange/routing key van de store.
- `exchange.achievements` (topic, durable) — outbound achievement events

---

### 4.3 Inkomende events

#### Event: `GameAchievementEvent`

Wordt geconsumeerd door `GameplayEventListener` op `queue.user.gameplay`.

- **Routing pattern:** `game.#.achievement`
- **Payload**

```json
{
  "achievementCode": "string",
  "gameName": "string",
  "gameId": "uuid",
  "playerId": "uuid",
  "occurredAt": "2026-01-01T10:00:00Z"
}
```

**Effect:** user-service kent achievement toe via:

- `profileId = playerId`
- `gameName`
- `achievementKey = achievementCode`

#### Event: `GamesPurchasedEvent`

Wordt geconsumeerd door `GamesPurchasedListener` op `queue.user-service.games-purchased`.

- **Payload**

```json
{
  "userId": "uuid",
  "gameIds": ["uuid", "uuid"]
}
```

**Effect:** voor elke `gameId` wordt de game toegevoegd aan de library. Fouten per game worden gelogd, de rest gaat door.

---

### 4.4 Uitgaande events

#### Event: `AchievementUnlockedEvent`

Wordt gepubliceerd door `AchievementEventPublisher` naar `exchange.achievements`.

- **Routing key:** `achievement.unlocked`
- **Payload**

```json
{
  "playerId": "uuid",
  "gameId": "uuid",
  "achievementKey": "string",
  "achievementName": "string",
  "description": "string"
}
```

**Gebruik door consumers (voorbeelden):**

- Communication-service: notificatie sturen
- Social-service: feed/updates
- Analytics: metrics verzamelen

---

## 5) Security

### 5.1 CORS

- Allowed origins via `cors.allowed-origins`.
- Methods: `GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD`
- Headers: `*`
- Credentials: toegestaan

### 5.2 Authorization rules

- `/api/profiles/me` → `authenticated()`
- `anyRequest()` → `permitAll()`

**Belangrijke opmerking** Meerdere endpoints (bv. `/api/library/me`, `/api/achievements/me`, `/api/profiles/me/preferences/**`) gebruiken `@AuthenticationPrincipal Jwt` en zijn functioneel bedoeld als **auth required**. Als je dit ook effectief wil afdwingen, breid `SecurityConfig` uit (bv. matchers voor `/api/library/**`, `/api/achievements/me`, `/api/profiles/me/**`).

---

## 6) Error handling & HTTP status codes

Er is een globale `@ControllerAdvice` (`ExceptionController`). Responses zijn **plain-text**.

### 404 NOT FOUND

- `ClaimNotFoundException`
- `ProfileNotFoundException`
- `ExternalGameNotFoundException`

### 401 UNAUTHORIZED

- `NotAuthenticatedException`

### 400 BAD REQUEST

- `LibraryException`
- `AchievementException`
- `IllegalArgumentException`
- `MethodArgumentTypeMismatchException` (bv. invalid UUID in path)

### 503 SERVICE UNAVAILABLE

- `GameServiceNotReachableException`

### 500 INTERNAL SERVER ERROR

- Alle andere exceptions: `Internal server error: <message>`

---

## 7) Integratie-notities voor externe game developers

### 7.1 Library integreren met aankoopflow

- De user-service verwacht een **purchase event** met `userId` en `gameIds` om library entries te creëren.
- Zorg dat de store-service (of commerce module) `GamesPurchasedEvent` publiceert en dat:
  - de queue `queue.user-service.games-purchased` correct gebind is,
  - payload compatibel is met het contract hierboven.

### 7.2 Achievements integreren met gameplay

- Publiceer tijdens/na gameplay een event dat matcht op `game.#.achievement` met:
  - `playerId` (UUID van de speler)
  - `gameId`
  - `achievementCode` (platform-brede achievement key/code)
  - `occurredAt`

User-service zal:

1. achievement toekennen,
2. (optioneel) de games-service raadplegen voor detail-info,
3. `achievement.unlocked` publiceren.

### 7.3 Achievements metadata (codes, namen, levels)

- Game developers definiëren achievements in de games-service (registratie), of via de eigen game-config afhankelijk van de platform-afspraken.
- Gebruik stabiele `achievementCode` values; wijzig deze niet na release.

### 7.4 ACL (anti-corruption layer)

Voor bestaande games met legacy achievement systemen is een **ACL** nuttig om:

- legacy achievement identifiers te mappen naar platform `achievementCode`
- timing/levels/semantiek gelijk te trekken

Voor nieuwe games: publiceer de gameplay events volgens het contract en zorg dat achievement codes overeenkomen met wat in het platform geregistreerd is.


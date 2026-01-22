# Integratiedocumentatie

# Games-service

## 1) Overzicht
De **Games-service** beheert de **registratie, configuratie en opstart** van games binnen het platform.

Belangrijkste verantwoordelijkheden:
- **Game registry**: games aanmaken/registreren met metadata (title/description/image), URLs en endpoints.
- **Game discovery**: lijst + details opvragen (op id of naam).
- **Start game**: een game-instance starten voor een lobby (met spelers en settings).
- **Settings**: schema/defaults aanbieden + settings valideren/resolven.
- **Achievements**: achievements per game opvragen.

> Authenticatie: `POST /api/games` (start-game) gebruikt `@AuthenticationPrincipal Jwt` en verwacht dus een **Bearer JWT**.

---

## 2) REST API
**Controller:** `GameController`  \
**Base path:** `/api/games`

### 2.1 Start game
#### POST `/api/games`
**Doel:** Start een game-instance voor een lobby.

- **Auth:** vereist (JWT)
- **Request body:** `StartGameRequest`
- **Response:** `202 Accepted` met `StartGameResponseModel`

**Request**
```json
{
  "lobbyId": "b3b6cf4e-8c4b-4c5c-9f29-5aa9d9b8b2a1",
  "gameId": "2f02b1d8-2b7d-4c2f-9b2b-9d9cbe2f4b1b",
  "players": [
    "2c4a8f7b-1e8b-4a02-bb54-6efc0b5c2b11",
    "9a77a7d0-4f0d-4b42-8d83-7f9e8bba9f3b"
  ],
  "hasBot": false,
  "settings": {
    "timeLimit": 300,
    "difficulty": "NORMAL"
  }
}
```

**Response**
```json
{ "gameInstanceId": "7b7e4a9d-6d8b-4a1d-a9d9-2b9a7f2c3f0e" }
```

**Opmerking (belangrijk voor integratie):**
- In de controller wordt gelogd op `lobbyId` en `gameId`.

---

### 2.2 Games opvragen
#### GET `/api/games`
**Doel:** Lijst alle geregistreerde games.

- **Auth:** permitAll (zie SecurityConfig)
- **Response:** `200 OK` met `GameListModel`

**Response**
```json
{
  "games": [
    {
      "id": "2f02b1d8-2b7d-4c2f-9b2b-9d9cbe2f4b1b",
      "name": "chess",
      "title": "Chess",
      "description": "Classic chess.",
      "image": "https://…",
      "frontendUrl": "https://…",
      "startEndpoint": "/api/start",
      "healthy": true,
      "lastHealthCheck": "2026-01-01T10:00:00Z",
      "createdAt": "2025-12-01T12:00:00Z",
      "updatedAt": "2025-12-15T12:00:00Z"
    }
  ]
}
```

#### GET `/api/games/{value}`
**Doel:** Haal details van één game op via **id of naam**.

- **Auth:** permitAll
- **Path param:** `value: string`
  - Eerst wordt geprobeerd `GameId.from(value)` (id/UUID-string)
  - Als dat faalt: zoek op `name`
- **Response:** `200 OK` met `GameDetailsModel`

---

### 2.3 Games registreren/aanmaken
Er zijn twee endpoints die een game kunnen creëren/registreren.

#### PUT `/api/games/{name}`
**Doel:** Registreer een game onder een gegeven naam.

- **Auth:** permitAll (zie SecurityConfig; in productie typisch admin-only)
- **Path param:** `name: string`
- **Request body:** `RegisterGameRequest`
- **Response:** `200 OK` met `GameDetailsModel`

> Opmerking: `RegisterGameRequest` bevat ook een `name` veld. Hou rekening met mogelijke validatie/consistentie (pad vs body) in de service-implementatie.

#### POST `/api/games/register`
**Doel:** Maak een nieuwe game aan.

- **Auth:** permitAll (zie SecurityConfig; in productie typisch admin-only)
- **Request body:** `RegisterGameRequest`
- **Response:** `200 OK` met `GameDetailsModel`

**Request (RegisterGameRequest)**
```json
{
  "name": "mygame",
  "backendUrl": "https://mygame-backend.example.com",
  "frontendUrl": "https://mygame.example.com",
  "startEndpoint": "/api/start",
  "healthEndpoint": "/actuator/health",

  "title": "My Game",
  "description": "Fun multiplayer game.",
  "image": "https://cdn.example.com/mygame.png",
  "price": 9.99,
  "category": "STRATEGY",

  "settingsDefinition": {
    "settings": [
      {
        "name": "timeLimit",
        "type": "INTEGER",
        "required": true,
        "min": 60,
        "max": 600,
        "allowedValues": null,
        "defaultValue": 300
      }
    ]
  },

  "achievements": [
    { "key": "win_1", "name": "First Win", "description": "Win a match", "levels": 1 }
  ]
}
```

---

### 2.4 Achievements
#### GET `/api/games/{id}/achievements`
**Doel:** Haal alle achievements voor een game op.

- **Auth:** permitAll
- **Path param:** `id: UUID` (= `gameId`)
- **Response:** `200 OK` met `AchievementListModel`

#### GET `/api/games/{id}/achievements/{key}`
**Doel:** Haal één achievement op via key.

- **Auth:** permitAll
- **Path params:**
  - `id: UUID` (= `gameId`)
  - `key: string`
- **Response:** `200 OK` met `AchievementDetailsModel`

---

### 2.5 Settings schema & validatie
#### GET `/api/games/{id}/settings`
**Doel:** Haal het **settings schema** op (definities + defaults) voor een game.

- **Auth:** permitAll
- **Path param:** `id: UUID` (= `gameId`)
- **Response:** `200 OK` met `GameSettingsSchemaModel`

#### POST `/api/games/{id}/settings/validate`
**Doel:** Valideer en **resolve** settings volgens het schema van de game.

- **Auth:** permitAll
- **Path param:** `id: UUID` (= `gameId`)
- **Request body:** `ValidateGameSettingsRequest` (optioneel; body kan ontbreken)
- **Response:** `200 OK` met `ValidateGameSettingsResponse`

**Request**
```json
{ "settings": { "timeLimit": 120 } }
```

**Response**
```json
{ "resolvedSettings": { "timeLimit": 120, "difficulty": "NORMAL" } }
```

> Als `request` of `request.settings` null is, wordt met een lege map gevalideerd. Defaults kunnen dan ingevuld worden door de resolver.

---

## 3) Models

### 3.1 Start game
#### `StartGameRequest`
```json
{
  "lobbyId": "uuid",
  "gameId": "uuid",
  "players": ["uuid"],
  "hasBot": true,
  "settings": { "any": "value" }
}
```

#### `StartGameResponseModel`
```json
{ "gameInstanceId": "uuid" }
```

### 3.2 Game registry
#### `GameDetailsModel`
Velden:
- `id: UUID`
- `name: string` (unieke slug/identifier)
- `title/description/image: string`
- `frontendUrl: string`
- `startEndpoint: string`
- `healthy: boolean`
- `lastHealthCheck: instant`
- `createdAt/updatedAt: instant`

#### `GameListModel`
```json
{ "games": [ /* GameDetailsModel */ ] }
```

#### `RegisterGameRequest`
```json
{
  "name": "string",
  "backendUrl": "string",
  "frontendUrl": "string",
  "startEndpoint": "string",
  "healthEndpoint": "string",

  "title": "string",
  "description": "string",
  "image": "string",
  "price": 9.99,
  "category": "STRATEGY",

  "settingsDefinition": { /* GameSettingsDefinition */ },
  "achievements": [
    { "key": "string", "name": "string", "description": "string", "levels": 0 }
  ]
}
```

#### `RegisterAchievementRequest`
```json
{ "key": "string", "name": "string", "description": "string", "levels": 0 }
```

### 3.3 Achievements
#### `AchievementDetailsModel`
```json
{ "key": "string", "name": "string", "description": "string", "levels": 1 }
```

#### `AchievementListModel`
```json
{ "achievements": [ /* AchievementDetailsModel */ ] }
```

### 3.4 Settings
#### `GameSettingsSchemaModel`
```json
{
  "settings": [
    {
      "name": "string",
      "type": "STRING|INTEGER|BOOLEAN|...",
      "required": true,
      "min": 0,
      "max": 10,
      "allowedValues": ["A", "B"],
      "defaultValue": "A"
    }
  ],
  "defaults": {
    "timeLimit": 300
  }
}
```

#### `ValidateGameSettingsRequest`
```json
{ "settings": { "any": "value" } }
```

#### `ValidateGameSettingsResponse`
```json
{ "resolvedSettings": { "any": "value" } }
```

---

## 4) Security

### 4.1 CORS
- Allowed origins via `cors.allowed-origins`.
- Methods: `GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD`
- Headers: `*`
- Credentials: toegestaan

### 4.2 Authorization rules (zoals geconfigureerd)
- Alleen `/api/profiles/me` is `authenticated()`.
- `anyRequest()` is `permitAll()`.

**Gevolgen**
- De meeste `/api/games/**` endpoints lijken publiek toegankelijk volgens deze config.
- `POST /api/games` (start-game) gebruikt wél een JWT principal; zonder JWT zal dit endpoint in praktijk falen (of token null zijn) afhankelijk van de resource-server setup.

---

## 5) Messaging / events
In de meegegeven code-snippets voor de Games-service is **geen RabbitMQ/Kafka publisher** of event-model zichtbaar.

- Als de Games-service wél events publiceert (bv. `game.started`, `game.instance.created`, …), voeg dan in dit document een sectie toe met:
  - exchange/topic
  - routing keys
  - payload-contracten
  - versie-/compatibiliteitsafspraken

---

## 6) Error handling & HTTP status codes
De Games-service gebruikt een `@RestControllerAdvice` (`GameExceptionHandler`) die fouten mapt naar **plain-text** responses.

### 404 NOT FOUND
- `GameNotFoundException`
- `AchievementNotFoundException`

### 400 BAD REQUEST
- `InvalidGameSettingsException`
- `PlayersListEmptyException`
- `DuplicateGameNameException`
- `GameUnhealthyException`
- `IllegalArgumentException`
- `HttpMessageNotReadableException`
  - Als oorzaak `InvalidTypeIdException`: `Unsupported game settings type: <typeId>`
  - Anders: `Malformed JSON request`

---

## 7) Integratie-notities voor externe game developers

### 7.1 Een nieuw spel integreren
Als externe developer lever je in essentie:
1. **Een stabiele game-identifier** (`gameId: UUID`) die platformbreed gebruikt wordt.
2. **Game backend** bereikbaar via `backendUrl` met:
   - een `startEndpoint` om een nieuwe game-instance te starten
   - een `healthEndpoint` om health checks uit te voeren
3. **Game frontend** via `frontendUrl`.
4. **Store metadata**: `title`, `description`, `image`, plus `price` en `category`.
5. (Optioneel) **Settings schema** (`settingsDefinition`) en **achievements**.

### 7.2 Settings: contract & validatie
- Definieer settings via `settingsDefinition` (type/required/min/max/allowedValues/defaultValue).
- Clients kunnen schema ophalen via `GET /api/games/{id}/settings`.
- Clients kunnen settings laten valideren/resolven via `POST /api/games/{id}/settings/validate`.

### 7.3 Achievements
- Achievements worden per game beheerd en zijn opvraagbaar via:
  - `GET /api/games/{id}/achievements`
  - `GET /api/games/{id}/achievements/{key}`

### 7.4 ACL (anti-corruption layer)
Voor bestaande games die al een eigen contract/API hadden vóór het platform kan een **ACL** nodig zijn om:
- bestaande identifiers te mappen naar `gameId`
- bestaande settings/achievements te vertalen naar het platform-schema

Voor **nieuwe** games ligt de verantwoordelijkheid van integratie bij de developer: registreer de game met correcte endpoints/metadata en implementeer de backend volgens de verwachtingen van de start/health flow.


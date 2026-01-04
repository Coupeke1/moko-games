# Integratiedocumentatie

# Session-service (Lobbies & realtime updates)

## 1) Overzicht
De **Session-service** beheert **lobbies/sessions** voor games:

- Lobby aanmaken en ophalen
- Lobby settings wijzigen (max players + game settings)
- Spelers beheren (ready/unready, remove, bot toevoegen/verwijderen)
- Invite flow (invite, accept, check invited)
- Lobby starten (koppeling naar game instance)
- Realtime updates richting clients via RabbitMQ → socket gateway (user-direct exchange / subscription exchange)
- Afhandeling van `game.ended` om lobby te **finishen**

Deze service is doorgaans de “coördinator” tussen:
- **Front-end** (lobby UI)
- **Games-service** (start game-instance)
- **Socket/communication layer** (realtime lobby updates)
- **Gameplay events** (game ended)

---

## 2) REST API

### 2.1 Lobby aanmaken, ophalen, sluiten en starten
**Controller:** `LobbyController`  \
**Base path:** `/api/lobbies`

#### POST `/api/lobbies`
**Doel:** Maak een lobby aan voor een game.

- **Auth:** vereist (JWT)
- **Request body:** `CreateLobbyModel`
- **Response:** `201 Created` met `LobbyModel`

**Request**
```json
{
  "gameId": "2f02b1d8-2b7d-4c2f-9b2b-9d9cbe2f4b1b",
  "maxPlayers": 4,
  "settings": {
    "timeLimit": 300,
    "difficulty": "NORMAL"
  }
}
```

#### GET `/api/lobbies/{id}`
**Doel:** Haal lobby details op.

- **Auth:** volgens `SecurityConfig` matcht `/api/lobbies/**` op `authenticated()`.
- **Path param:** `id: UUID` (= `lobbyId`)
- **Response:** `200 OK` met `LobbyModel`

**Business rule:** als lobby `closed` of `finished` is, wordt `LobbyClosedException` gegooid.

#### PUT `/api/lobbies/{id}/settings`
**Doel:** Update lobby settings (max players en/of game settings).

- **Auth:** vereist (JWT)
- **Path param:** `id: UUID`
- **Request body:** `UpdateLobbySettingsModel`
- **Response:** `200 OK` met `LobbyModel`

#### POST `/api/lobbies/{id}/close`
**Doel:** Sluit een lobby.

- **Auth:** vereist (JWT)
- **Path param:** `id: UUID`
- **Response:** `200 OK` met `LobbyModel`

#### POST `/api/lobbies/{id}/start`
**Doel:** Start de lobby (start game) en retourneer de `startedGameId`.

- **Auth:** vereist (JWT)
- **Path param:** `id: UUID`
- **Response:** `200 OK` met **plain String** (= `gameId` als string)

> Opmerking: de lobby bewaart `startedGameId`. Als het start-proces faalt, kan `GameNotStartedException` voorkomen.

---

### 2.2 Spelers beheren
**Controller:** `PlayerController`  
**Base path:** `/api/lobbies`

#### GET `/api/lobbies/{id}/players`
**Doel:** Haal lijst van players (ids) in de lobby op.

- **Auth:** in `SecurityConfig` staat expliciet `/api/lobbies/*/players` op `permitAll()`.
- **Response:** `200 OK` met `PlayerId[]` (uuid wrappers)

**Business rule:** als lobby closed/finished is → `LobbyClosedException`.

#### DELETE `/api/lobbies/{lobbyId}/players/{playerId}`
**Doel:** Verwijder een player uit de lobby.

- **Auth:** vereist (JWT)
- **Path params:** `lobbyId: UUID`, `playerId: UUID`
- **Response:** void

#### PATCH `/api/lobbies/{lobbyId}/players/ready`
**Doel:** Zet de ingelogde speler op ready.

- **Auth:** vereist (JWT)
- **Response:** void

#### PATCH `/api/lobbies/{lobbyId}/players/unready`
**Doel:** Zet de ingelogde speler op unready.

- **Auth:** vereist (JWT)
- **Response:** void

#### DELETE `/api/lobbies/{id}/bot`
**Doel:** Verwijder de bot uit de lobby.

- **Auth:** vereist (JWT)
- **Response:** `200 OK` met `LobbyModel`

---

### 2.3 Invites & bots
**Controller:** `InviteController`  \
**Base path:** `/api/lobbies`

#### POST `/api/lobbies/{id}/invite/{playerId}`
**Doel:** Invite een speler naar een lobby.

- **Auth:** vereist (JWT)
- **Path params:**
  - `id: UUID` (= lobbyId)
  - `playerId: UUID` (= target user)
- **Response:** void

> In de service layer wordt `token` meegegeven: verwacht dat er checks gebeuren (owner/permissions) en dat er een event kan gepubliceerd worden.

#### POST `/api/lobbies/{id}/invite/accept/me`
**Doel:** Accepteer een invite voor lobby `{id}` als ingelogde user.

- **Auth:** vereist (JWT)
- **Path param:** `id: UUID`
- **Response:** void

#### GET `/api/lobbies/{lobbyId}/invited/{userId}`
**Doel:** Check of user `{userId}` op invited-lijst staat voor lobby `{lobbyId}`.

- **Auth:** permitAll (endpoint gebruikt geen token)
- **Response:** `200 OK` met `true|false`

#### POST `/api/lobbies/{id}/invite/bot`
**Doel:** Voeg een bot toe aan de lobby.

- **Auth:** vereist (JWT)
- **Response:** `200 OK` met `LobbyModel`

---

### 2.4 Query endpoints
**Controller:** `QueryController`  \
**Base path:** `/api/lobbies`

#### GET `/api/lobbies`
**Doel:** Haal alle lobbies op.

- **Auth:** volgens `SecurityConfig` matcht dit op `/api/lobbies/**` en is dus authenticated.
- **Response:** `200 OK` met `LobbyModel[]`

#### GET `/api/lobbies/{lobbyId}/invited`
**Doel:** Haal alle invited players voor een lobby op.

- **Auth:** authenticated
- **Response:** `200 OK` met `UUID[]`

#### GET `/api/lobbies/invited/{gameId}/me`
**Doel:** Haal alle lobby invites op voor de ingelogde speler voor een bepaald spel.

- **Auth:** vereist (JWT)
- **Path param:** `gameId: UUID`
- **Response:** `200 OK` met `LobbyModel[]`

---

## 3) Models

### 3.1 Create/update
#### `CreateLobbyModel`
```json
{
  "gameId": "uuid",
  "maxPlayers": 4,
  "settings": { "any": "value" }
}
```

#### `UpdateLobbySettingsModel`
```json
{
  "maxPlayers": 4,
  "settings": { "any": "value" }
}
```

### 3.2 Lobby
#### `LobbyModel`
```json
{
  "id": "uuid",
  "gameId": "uuid",
  "ownerId": "uuid",
  "players": [
    { "id": "uuid", "username": "string", "image": "string", "ready": false }
  ],
  "bot": { "id": "uuid", "username": "string", "image": "string", "ready": true },
  "maxPlayers": 4,
  "status": "OPEN|CLOSED|FINISHED|...",
  "createdAt": "2026-01-01T10:00:00Z",
  "settings": { "any": "value" },
  "startedGameId": "uuid"
}
```

#### `PlayerModel`
```json
{ "id": "uuid", "username": "string", "image": "string", "ready": false }
```

> Players worden gesorteerd op `username` in de response.

---

## 4) Messaging (RabbitMQ) & realtime updates

### 4.1 Exchanges, queues & bindings
De session-service definieert meerdere exchanges/queues, vooral voor realtime socket updates.

**Exchanges**
- `exchange.session` (topic) — session events (invites/joined)
- `exchange.gameplay` (topic) — gameplay lifecycle events
- `user.direct.exchange` (topic) — direct messages per user richting socket gateway
- `socket.subscription.exchange` (topic) — subscription/handshake events vanuit socket gateway

**Queues**
- `queue.session.game.ended` — consumer voor `game.ended`
- `user.socket.queue` — consumer (binding) op user messages routing key
- `user.subscribe.queue` — consumer (binding) op subscription routing key

**Bindings**
- `queue.session.game.ended` ← `exchange.gameplay` met routing key `game.ended`
- `user.socket.queue` ← `user.direct.exchange` met routing key `user.message`
- `user.subscribe.queue` ← `socket.subscription.exchange` met routing key `socket.subscribe.user.queue.lobby`

---

### 4.2 Uitgaande session events
#### Event: `LobbyInviteEvent`
Gepubliceerd door `LobbyEventPublisher`.

- **Exchange:** `exchange.session`
- **Routing key:** `session.lobby.invite`
- **Payload**
```json
{
  "gameName": "string",
  "targetUserId": "uuid",
  "inviterName": "string",
  "inviterId": "uuid",
  "lobbyId": "uuid"
}
```

**Doel:** consumers (bv. notification/communication) kunnen invite notificaties sturen.

#### Event: `PlayerJoinedLobbyEvent`
Gepubliceerd door `LobbyEventPublisher`.

- **Exchange:** `exchange.session`
- **Routing key:** `session.lobby.joined`
- **Payload**
```json
{
  "lobbyId": "uuid",
  "playerId": "uuid",
  "playerName": "string",
  "hostUserId": "uuid"
}
```

---

### 4.3 Inkomende gameplay events
#### Event: `GameEndedEvent`
Geconsumeerd door `GameEndedListener` via `queue.session.game.ended`.

- **Exchange:** `exchange.gameplay`
- **Routing key:** `game.ended`
- **Payload**
```json
{
  "instanceId": "uuid",
  "occurredAt": "2026-01-01T10:00:00Z"
}
```

**Effect:**
- Lobby wordt opgezocht op `startedGameId == instanceId`.
- Lobby status wordt naar “finished” gezet (`lobby.finish()`).
- Vervolgens `publisher.saveAndPublish(lobby)` (persist + realtime push).

---

### 4.4 Realtime lobby updates richting clients
De session-service pusht lobby state naar users via **direct user messages** (RabbitMQ) die door een socket gateway/websocket service kunnen worden uitgezonden.

#### Message: `LobbyMessage`
Wordt verstuurd naar `user.direct.exchange` met routing key `user.message`.

```json
{
  "userId": "uuid",
  "queue": "lobby",
  "payload": { /* LobbyModel */ }
}
```

**Publishers**
- `LobbyPublisher.publishToPlayers(lobby)` → stuurt lobby model naar alle players in de lobby.
- `LobbyListener.publishToPlayer(playerId, lobby)` → stuurt lobby model naar één player.

**Subscription flow**
- `LobbyListener.subscribeListener` luistert op `user.subscribe.queue`.
- Payload: `SubscribeMessage`.

```json
{ "userId": "uuid", "destination": "string", "sessionId": "string" }
```

- Wanneer een user zich “subscribe’t” (bv. op lobby updates), zoekt session-service de lobby van die user (`service.findByPlayer`) en pusht onmiddellijk de lobby state naar die user.

---

## 5) Security

### 5.1 CORS
- Allowed origins via `cors.allowed-origins`.
- Methods: `GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD`
- Headers: `*`
- Credentials: toegestaan

### 5.2 Authorization rules (zoals geconfigureerd)
- `/ws/**` → permitAll
- `/actuator/health/**` → permitAll
- `/api/lobbies/**` → authenticated
- `/api/lobbies/*/players` → permitAll (expliciete uitzondering)
- `anyRequest()` → permitAll

---

## 6) Error handling & HTTP status codes
In de meegegeven snippets is geen centrale `@ControllerAdvice` voor session-service meegegeven.

Wel zijn er domain exceptions zichtbaar:
- `LobbyClosedException` (bij GET lobby en GET players als closed/finished)
- `GameNotStartedException` (als start geen startedGameId oplevert)
- `LobbyNotFoundByGameInstanceException` (bij `game.ended` consumer)

**Aanbevolen mapping (conventie):**
- `LobbyClosedException` → `400` of `410 Gone` (afhankelijk van gewenste semantics)
- `LobbyNotFoundByGameInstanceException` → `404`
- `GameNotStartedException` → `400`

> Als er in jullie codebase wél een ExceptionHandler bestaat, voeg die mapping hier toe.

---

## 7) Integratie-notities voor externe game developers

### 7.1 Lobby lifecycle (client)
1. **Create lobby**: `POST /api/lobbies` met `gameId` en settings.
2. **Invite**: `POST /api/lobbies/{id}/invite/{playerId}` of voeg bot toe via `/invite/bot`.
3. Players zetten **ready/unready**.
4. **Start**: host start via `POST /api/lobbies/{id}/start`.
5. Gameplay loopt; bij einde publiceert gameplay component `game.ended`.
6. Session-service markeert lobby als finished en pusht state naar players.

### 7.2 Realtime lobby updates
- Als je een socket gateway hebt: laat clients subscriben op lobby updates.
- Socket gateway publiceert een `SubscribeMessage` naar `socket.subscription.exchange` met routing key `socket.subscribe.user.queue.lobby`.
- Session-service pusht lobby snapshots als `LobbyMessage` naar `user.direct.exchange` met routing key `user.message`.

### 7.3 Events voor notificaties
- Subscribe op `exchange.session`:
  - `session.lobby.invite` → invite notificaties
  - `session.lobby.joined` → “player joined” updates

### 7.4 ACL (anti-corruption layer)
Voor bestaande games met een legacy lobby systeem kan een ACL nodig zijn om:
- legacy session ids te mappen naar `lobbyId`
- bestaande matchmaking/join flows te vertalen naar deze endpoints/events

Voor nieuwe integraties volstaat de lobby lifecycle via de REST endpoints + optioneel realtime updates via de messaging-contracten hierboven.


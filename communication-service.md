# Integratiedocumentatie



# Communication-service

## 1) Overzicht

De **Communication-service** biedt:

- **Chat-functionaliteit** (lobby-chat, friends/DM chat, bot/AI chat)
- **Notifications** (op basis van events die door andere services gepubliceerd worden)



> Authenticatie: meerdere endpoints gebruiken `@AuthenticationPrincipal Jwt token` en halen `token.getSubject()` op als **(player) user-id**. Verwacht dus een **Bearer JWT** (OAuth2 Resource Server) voor die endpoints.

---

## 2) REST API

### 2.1 Chat – bot (AI/game bot chat)

**Controller:** `BotChatController`\
**Base path:** `/api/chat/bot`

#### POST `/api/chat/bot`

**Doel:** Maak een bot chat channel aan voor de ingelogde gebruiker.

- **Auth:** vereist (JWT)
- **Request body:** geen
- **Response:** `200 OK` met `ChatChannelResponse`

\*\*Response model: \*\*``

```json
{
  "id": "uuid",
  "type": "BOT|...",
  "referenceId": "string"
}
```

#### POST `/api/chat/bot/{channelId}/messages`

**Doel:** Stuur een bericht naar een bot-channel.

- **Auth:** vereist (JWT)
- **Path param:** `channelId: UUID`
- **Request body:** `BotMessageRequestModel`

\*\*Request model: \*\*``

```json
{
  "content": "string",
  "gameName": "string"
}
```

- **Response:** `200 OK` met `ChatMessageResponse`

#### GET `/api/chat/bot/{channelId}/messages?since=...`

**Doel:** Haal berichten op uit een bot-channel.

- **Auth:** vereist (JWT)
- **Query param (optioneel):** `since: Instant` (ISO-8601, bv. `2026-01-01T10:15:30Z`)
- **Response:** `200 OK` met lijst `ChatMessageResponse[]`

---

### 2.2 Chat – channel aanmaken

**Controller:** `ChannelController`\
**Base path:** `/api/chat/channel`

> Let op: deze endpoints gebruiken geen `@AuthenticationPrincipal` in de controller omdat deze automatisch aangeroepen word als er een lobby aangemaakt word

#### POST `/api/chat/channel/lobby/{lobbyId}`

**Doel:** Maak een lobby chat channel aan.

- **Path param:** `lobbyId: String`
- **Response:** `200 OK` met `ChatChannelResponse`

#### POST `/api/chat/channel/bot/{referenceId}`

**Doel:** Maak een AI/bot chat channel aan met een reference.

- **Path param:** `referenceId: String`
- **Response:** `200 OK` met `ChatChannelResponse`

---

### 2.3 Chat – friends (direct messages)

**Controller:** `FriendsChatController`\
**Base path:** `/api/chat/friends`

#### POST `/api/chat/friends/{friendId}`

**Doel:** Stuur een DM naar een friend-chat (kanaaltype `FRIENDS`).

- **Auth:** vereist (JWT)
- **Path param:** `friendId: UUID`
- **Request body:** `ChatMessageRequestModel`

\*\*Request model: \*\*``

```json
{
  "content": "string"
}
```

- **Response:** `200 OK` met `ChatMessageResponse`

#### GET `/api/chat/friends/{friendId}?since=...`

**Doel:** Haal DM’s op met een bepaalde friend.

- **Auth:** vereist (JWT)
- **Query param (optioneel):** `since: Instant` (ISO-8601)
- **Response:** `200 OK` met `ChatMessageResponse[]`

---

### 2.4 Chat – lobby

**Controller:** `LobbyChatController`\
**Base path:** `/api/chat/lobby`

#### POST `/api/chat/lobby/{lobbyId}`

**Doel:** Stuur een bericht naar lobby-chat (kanaaltype `LOBBY`).

- **Auth:** vereist (JWT)
- **Path param:** `lobbyId: String`
- **Request body:** `ChatMessageRequestModel`
- **Response:** `200 OK` met `ChatMessageResponse`

#### GET `/api/chat/lobby/{lobbyId}?since=...`

**Doel:** Haal lobby-chat berichten op.

- **Auth:** vereist (JWT)
- **Query param (optioneel):** `since: Instant` (ISO-8601)
- **Response:** `200 OK` met `ChatMessageResponse[]`

---

### 2.5 Models – chat responses

#### `ChatMessageResponse`

```json
{
  "id": "uuid",
  "senderId": "string",
  "content": "string",
  "timestamp": "2026-01-01T10:15:30Z"
}
```

#### `ChatHistoryResponse` *(bestaat als model, maar niet in de getoonde controllers gebruikt)*

```json
{
  "channelId": "uuid",
  "type": "string",
  "referenceId": "string",
  "messages": [ { "id": "uuid", "senderId": "string", "content": "string", "timestamp": "..." } ]
}
```

---

## 3) Notifications API

**Controller:** `NotificationController`\
**Base path:** `/api/notifications`

### GET `/api/notifications`

**Doel:** Paginated lijst van notifications voor de ingelogde gebruiker.

- **Auth:** vereist (JWT)
- **Query params:**
  - `type` *(optioneel)*: `NotificationFilter` (app-layer enum/filter)
  - `origin` *(optioneel)*: `NotificationOrigin` (domain enum)
  - `page` *(default: 0)*
  - `size` *(default: 10)*
- **Response:** `200 OK` met `PagedResponse<NotificationModel>`

\*\*Response: \*\*``

```json
{
  "items": [
    {
      "id": "uuid",
      "origin": "DIRECT_MESSAGE|LOBBY_INVITE|...",
      "title": "string",
      "message": "string",
      "createdAt": "2026-01-01T10:15:30Z",
      "read": false
    }
  ],
  "page": 0,
  "size": 10,
  "last": false
}
```

### GET `/api/notifications/since?time=...`

**Doel:** Haal alle notifications sinds een bepaald tijdstip (handig voor polling).

- **Auth:** vereist (JWT)
- **Query param:** `time: Instant` (ISO-8601)
- **Response:** `200 OK` met `NotificationModel[]`

### PATCH `/api/notifications/{id}/read`

**Doel:** Markeer notification als gelezen.

- **Auth:** (niet zichtbaar in controller signature; meestal wel via security-config)
- **Path param:** `id: UUID`
- **Response:** `200 OK` zonder body

---

## 4) Messaging (RabbitMQ)

### 4.1 Exchanges

De service definieert volgende **TopicExchanges**:

- `exchange.social`
- `exchange.session`
- `exchange.achievements`
- `exchange.store`
- `exchange.chat`
- `user.direct.exchange`

### 4.2 Queues

- `queue.notifications.friend-request-received`
- `queue.notifications.friend-request-accepted`
- `queue.notifications.lobby-invite`
- `queue.notifications.player-joined-lobby`
- `queue.notifications.achievement-unlocked`
- `queue.notifications.order-completed`
- `queue.notifications.direct-message`
- `user.socket.queue`

### 4.3 Routing keys & bindings

| Exchange                | Queue                                         | Routing key                      |
| ----------------------- | --------------------------------------------- | -------------------------------- |
| `exchange.social`       | `queue.notifications.friend-request-received` | `social.friend-request.received` |
| `exchange.social`       | `queue.notifications.friend-request-accepted` | `social.friend-request.accepted` |
| `exchange.session`      | `queue.notifications.lobby-invite`            | `session.lobby.invite`           |
| `exchange.session`      | `queue.notifications.player-joined-lobby`     | `session.lobby.joined`           |
| `exchange.achievements` | `queue.notifications.achievement-unlocked`    | `achievement.unlocked`           |
| `exchange.store`        | `queue.notifications.order-completed`         | `store.order.completed`          |
| `exchange.chat`         | `queue.notifications.direct-message`          | `chat.direct-message`            |
| `user.direct.exchange`  | `user.socket.queue`                           | `user.message`                   |

### 4.4 JSON (de)serialisatie

`RabbitMQConfig` zet `Jackson2JsonMessageConverter` als message converter voor listeners en publishing.

---

## 5) Inkomende events → Notifications

De Communication-service **consumeert** events en maakt notifications aan via `NotificationService`.

### 5.1 Achievements

**Listener:** `AchievementNotificationListener`\
**Queue:** `queue.notifications.achievement-unlocked`

**Event:** `AchievementUnlockedEvent`

```json
{
  "playerId": "uuid",
  "achievementId": "uuid",
  "achievementName": "string",
  "description": "string"
}
```

**Notification origin:** `ACHIEVEMENT_UNLOCKED`\
**Titel:** `Achievement unlocked`\
**Message:** `achievementName`

### 5.2 Chat

**Listener:** `ChatNotificationListener`\
**Queue:** `queue.notifications.direct-message`

**Event:** `DirectMessageReceivedEvent`

```json
{
  "senderId": "uuid",
  "senderName": "string",
  "recipientId": "uuid",
  "messagePreview": "string",
  "channelId": "uuid"
}
```

**Notification origin:** `DIRECT_MESSAGE`\
**Titel:** `Message from "{senderName}"`\
**Message:** `messagePreview`

### 5.3 Session

**Listener:** `SessionNotificationListener`\
**Queues:**

- `queue.notifications.lobby-invite`
- `queue.notifications.player-joined-lobby`

**Event:** `LobbyInviteEvent`

```json
{
  "lobbyId": "uuid",
  "inviterId": "uuid",
  "inviterName": "string",
  "targetUserId": "uuid",
  "gameName": "string"
}
```

**Notification origin:** `LOBBY_INVITE`\
**Titel:** `Invited to lobby`\
**Message:** `"{inviterName}" has invited you to a lobby for "{gameName}"!`

**Event:** `PlayerJoinedLobbyEvent`

```json
{
  "lobbyId": "uuid",
  "playerId": "uuid",
  "playerName": "string",
  "hostUserId": "uuid"
}
```

**Notification origin:** `PLAYER_JOINED_LOBBY`\
**Titel:** `Player joined`\
**Message:** `"{playerName}" has joined your lobby!`

### 5.4 Social

**Listener:** `SocialNotificationListener`\
**Queues:**

- `queue.notifications.friend-request-received`
- `queue.notifications.friend-request-accepted`

**Event:** `FriendRequestReceivedEvent`

```json
{
  "senderId": "uuid",
  "senderName": "string",
  "targetUserId": "uuid"
}
```

**Notification origin:** `FRIEND_REQUEST_RECEIVED`\
**Titel:** `New friend request`\
**Message:** `"{senderName}" has sent you a friend request!`

**Event:** `FriendRequestAcceptedEvent`

```json
{
  "senderId": "uuid",
  "senderName": "string",
  "targetUserId": "uuid"
}
```

**Notification origin:** `FRIEND_REQUEST_ACCEPTED`\
**Titel:** `Friend request accepted`\
**Message:** `"{senderName}" has accepted your friend request!`

### 5.5 Store

**Listener:** `StoreNotificationListener`\
**Queue:** `queue.notifications.order-completed`

**Event:** `OrderCompletedEvent`

```json
{
  "userId": "uuid",
  "orderId": "uuid",
  "totalAmount": 12.34
}
```

**Notification origin:** `ORDER_COMPLETED`\
**Titel:** `Order completed`\
**Message:** `Order has been completed for a total of €{totalAmount}!`

---

## 6) Uitgaande events

### 6.1 Direct message event publishing

**Publisher:** `ChatEventPublisher`

Wanneer de service een DM verwerkt (bv. via `FriendsChatController`/`ChatService`), kan ze een event publiceren:

- **Exchange:** `exchange.chat`
- **Routing key:** `chat.direct-message`
- **Payload:** `DirectMessageReceivedEvent`

Dit event wordt door de **ChatNotificationListener** geconsumeerd (zelfde service) om notifications te maken, maar kan ook door andere services geconsumeerd worden indien gewenst.

---

## 7) Integratie-notities voor externe game developers

### Bot/AI chat per game

- Voordat je deze kan gebruiken moet je eerst een samenvatting van je game met de regels,belangrijke informatie uploaden naar [https://team3.techtitans.be/upload-d](https://team3.techtitans.be/upload-d)ocument

### Notifications

- UI/clients kunnen notifications ophalen via `GET /api/notifications` of incrementieel via `GET /api/notifications/since?time=...`.
- Markeer als gelezen via `PATCH /api/notifications/{id}/read`.

### Events

- De service verwacht dat andere domeinen events publiceren met de juiste **routing keys** (zie tabel 4.3).\
  Als jouw game of game-service bijvoorbeeld een lobby invite/achievement concept heeft dat in notifications moet verschijnen, moet je events publiceren die qua payload overeenkomen met de records hierboven.

---

## 8) Security

### 8.1 Globale security rules (SecurityFilterChain)

- **CORS**: enabled via `corsConfigurationSource()`
  - `allowedOrigins` komt uit config property `cors.allowed-origins`
  - Methods: `GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD`
  - Headers: `*`
  - Credentials: toegestaan
  - `maxAge`: 3600s
- **CSRF**: disabled
- **Sessions**: `STATELESS` (JWT-only)
- **Authorization rules**:
  - `/ws/**` → **permitAll** (WebSocket endpoint is publiek bereikbaar) maar bevat interne validatie
  - `/actuator/health/**` → **permitAll**
  - `/api/**` → **authenticated** (alle REST endpoints onder `/api` vereisen een geldige JWT)
  - `anyRequest()` → permitAll
- **OAuth2 Resource Server**: JWT met custom `JwtAuthenticationConverter` (roles via `KeycloakRealmRoleConverter`).

**Gevolgen voor integratie**

- Alles onder **/api** vereist \*\*Authorization: Bearer \*\*.
- Zelfs endpoints die in de controller geen `@AuthenticationPrincipal` hebben (bv. `/api/chat/channel/**`) zijn toch **afgeschermd** door de globale rule `/api/** -> authenticated`.
- WebSocket (`/ws/**`) is publiek bereikbaar op filter-niveau; eventuele auth gebeurt dan typisch op WS-handshake/berichten-niveau (niet zichtbaar in deze snippet).

---

## 9) Error handling & HTTP status codes

### 9.1 Exception mapping (ExceptionController)

De service gebruikt een globale `@ControllerAdvice` die exceptions mapt naar status codes met een **plain-text body** (`String`), meestal `ex.getMessage()`.

> Body-formaat: **geen JSON error envelope**, maar tekst (String).

#### 403 FORBIDDEN

Wordt gegooid wanneer de gebruiker niet de nodige rechten/context heeft:

- `NotInLobbyException`
- `NotBotChannelOwnerException`
- `NotFriendsException`

**Response**

- Status: `403`
- Body: `ex.getMessage()`

#### 404 NOT FOUND

Wordt gebruikt wanneer een resource niet bestaat:

- `ChatChannelNotFoundException`
- `NotificationNotFoundException`
- `UserProfileNotFoundException`
- `UserPreferencesNotFoundException`

**Response**

- Status: `404`
- Body: `ex.getMessage()`

#### 400 BAD REQUEST

Wordt gebruikt voor invalid input/claims/business rules:

- `MessageEmptyException`
- `CantAutoCreateBotChannel`
- `BotMessageInLobbyException`
- `ClaimNotFoundException`

**Response**

- Status: `400`
- Body: `ex.getMessage()`

#### 502 BAD GATEWAY

- `BotServiceException`

**Response**

- Status: `502`
- Body: `ex.getMessage()`

#### 503 SERVICE UNAVAILABLE

- `ServiceUnavailableException`

**Response**

- Status: `503`
- Body: `ex.getMessage()`

#### 500 INTERNAL SERVER ERROR

- `EmailSendingException`
- `EmailTemplateException`
- Alle andere `RuntimeException`

**Response**

- Status: `500`
- Body:
  - Email exceptions: `ex.getMessage()`
  - Generic runtime: `Internal error: {ex.getMessage()}`

### 9.2 Auth errors (Spring Security)

Naast bovenstaande mappings kan Spring Security ook standaard responses geven:

- **401 Unauthorized**: ontbrekende/ongeldige JWT voor `/api/**`
- **403 Forbidden**: geldige JWT maar toegang geweigerd door security rules of method security

##

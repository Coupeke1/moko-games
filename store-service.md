# Integratiedocumentatie

# Store-service

## 1) Overzicht

De **Store-service** verzorgt de store-flow op het platform:

- **Catalogus** van games in de store (prijzen, categorieën, posts/marketing)
- **Shopping cart** per ingelogde gebruiker
- **Orders & betalingen** (checkout URL genereren, betaling verifiëren)
- **Uitgaande events** na aankoop (voor andere services zoals library/notifications)

> Authenticatie: meerdere endpoints gebruiken `@AuthenticationPrincipal Jwt token` en halen de `UserId` uit de JWT. Verwacht dus een **Bearer JWT** (OAuth2 Resource Server) voor user-gebonden acties (cart, order-create).

### Belangrijk concept: GameId + metadata

De Store-service werkt met een **GameId (UUID)** en verrijkt store-data met **game-metadata** (titel, beschrijving, afbeelding, …) via een aparte metadata-provider (in code: `GameService.getGameWithMetadata(GameId)`).

**Conventie:** éénzelfde `gameId` wordt platformbreed gebruikt (games-service, store, library, …).

---

## 2) REST API

### 2.1 Cart

**Controller:** `CartController`\
**Base path:** `/api/cart`

#### GET `/api/cart`

**Doel:** Haal de cart op (of maak ze aan als ze nog niet bestaat).

- **Auth:** vereist (JWT)
- **Request body:** geen
- **Response:** `200 OK` met `EntryModel[]`

**Response (voorbeeld)**

```json
[
  {
    "id": "2f02b1d8-2b7d-4c2f-9b2b-9d9cbe2f4b1b",
    "title": "My Awesome Game",
    "description": "Short description…",
    "image": "https://…",
    "price": 9.99,
    "category": "STRATEGY",
    "popularity": 0.72,
    "purchaseCount": 154
  }
]
```

#### POST `/api/cart`

**Doel:** Voeg een game toe aan de cart.

- **Auth:** vereist (JWT)
- **Request body:** `AddEntryModel`
- **Response:** `200 OK` zonder body

**Request**

```json
{ "id": "2f02b1d8-2b7d-4c2f-9b2b-9d9cbe2f4b1b" }
```

#### GET `/api/cart/{id}`

**Doel:** Controleer of een game al in de cart zit.

- **Auth:** vereist (JWT)
- **Path param:** `id: UUID` (= `gameId`)
- **Response:** `200 OK` met `true|false`

#### DELETE `/api/cart/{id}`

**Doel:** Verwijder één game uit de cart.

- **Auth:** vereist (JWT)
- **Path param:** `id: UUID` (= `gameId`)
- **Response:** `204 No Content`

#### DELETE `/api/cart`

**Doel:** Maak de cart leeg.

- **Auth:** vereist (JWT)
- **Response:** `204 No Content`

---

### 2.2 Catalogus (store games)

**Controller:** `CatalogController`\
**Base path:** `/api/store/games`

#### GET `/api/store/games`

**Doel:** Zoek en pagineer door de store-catalogus.

- **Auth:** volgens `SecurityConfig` is **exact** dit pad (`/api/store/games`) **authenticated**.
- **Query params:**
  - `query: string` (vrije tekst)
  - `category: GameCategory` (enum)
  - `minPrice: decimal`
  - `maxPrice: decimal`
  - `sort: string` (bv. `price`, `popularity`, … afhankelijk van implementatie)
  - `page: int` (default `0`)
  - `size: int` (default `10`)
- **Response:** `200 OK` met `PagedResponse<EntryModel>`

**Response**

```json
{
  "items": [
    {
      "id": "2f02b1d8-2b7d-4c2f-9b2b-9d9cbe2f4b1b",
      "title": "My Awesome Game",
      "description": "Short description…",
      "image": "https://…",
      "price": 9.99,
      "category": "STRATEGY",
      "popularity": 0.72,
      "purchaseCount": 154
    }
  ],
  "page": 0,
  "size": 10,
  "last": false
}
```

#### GET `/api/store/games/{id}`

**Doel:** Haal details van één store-entry op.

- **Auth:** volgens `SecurityConfig` valt dit onder `/api/store/games/**` en is dus **permitAll**.
- **Path param:** `id: UUID` (= `gameId`)
- **Response:** `200 OK` met `EntryModel`

#### GET `/api/store/games/{id}/posts`

**Doel:** Haal alle posts (marketing/nieuws) voor een game op.

- **Auth:** permitAll (volgens config)
- **Path param:** `id: UUID` (= `gameId`)
- **Response:** `200 OK` met `PostModel[]`

#### GET `/api/store/games/{entryId}/posts/{postId}`

**Doel:** Haal één specifieke post op.

- **Auth:** permitAll (volgens config)
- **Path params:**
  - `entryId: UUID` (= `gameId`)
  - `postId: UUID`
- **Response:** `200 OK` met `PostModel`

#### POST `/api/store/games`

**Doel:** Maak een nieuwe store-entry aan (koppeling tussen `gameId` en store-gegevens zoals prijs & categorie).

- **Auth:** let op: door de huidige `SecurityConfig` lijkt dit endpoint **permitAll** (want het matcht `/api/store/games/**`). In productie is dit typisch **admin-only**.
- **Request body:** `NewEntryModel`
- **Response:** `200 OK` met `EntryModel`

**Request**

```json
{
  "id": "2f02b1d8-2b7d-4c2f-9b2b-9d9cbe2f4b1b",
  "price": 9.99,
  "category": "STRATEGY"
}
```

#### PUT `/api/store/games/{id}`

**Doel:** Update prijs/categorie van een store-entry.

- **Auth:** permitAll (volgens config; typisch admin-only)
- **Path param:** `id: UUID` (= `gameId`)
- **Request body:** `UpdateEntryModel`
- **Response:** `200 OK` met `EntryModel`

**Request**

```json
{
  "price": 12.49,
  "category": "STRATEGY"
}
```

#### DELETE `/api/store/games/{id}`

**Doel:** Verwijder een store-entry.

- **Auth:** permitAll (volgens config; typisch admin-only)
- **Path param:** `id: UUID` (= `gameId`)
- **Response:** `204 No Content`

---

### 2.3 Orders

**Controller:** `OrderController`\
**Base path:** `/api/orders`

#### POST `/api/orders`

**Doel:** Maak een order aan op basis van de ingelogde gebruiker + genereer een payment checkout URL.

- **Auth:** in de controller wordt `@AuthenticationPrincipal Jwt token` gebruikt ⇒ in praktijk **JWT nodig**.
- **Request body:** geen
- **Response:** `200 OK` met **plain String** (= checkout URL)

**Response (voorbeeld)**

```text
https://www.mollie.com/payments/checkout/...
```

#### POST `/api/orders/{id}/verify`

**Doel:** Verifieer de betaling voor een order.

- **Auth:** niet zichtbaar in de controller-signature (kan publiek zijn voor webhooks)
- **Path param:** `id: UUID` (= `orderId`)
- **Response:** `200 OK` met `OrderResponseModel`

#### GET `/api/orders/{id}`

**Doel:** Haal een order op.

- **Auth:** niet zichtbaar in de controller-signature
- **Path param:** `id: UUID` (= `orderId`)
- **Response:** `200 OK` met `OrderResponseModel`

---

## 3) Models

### 3.1 Cart

#### `AddEntryModel`

```json
{ "id": "uuid" }
```

### 3.2 Catalogus

#### `EntryModel`

```json
{
  "id": "uuid",
  "title": "string",
  "description": "string",
  "image": "string",
  "price": 9.99,
  "category": "STRATEGY",
  "popularity": 0.72,
  "purchaseCount": 154
}
```

#### `NewEntryModel`

```json
{ "id": "uuid", "price": 9.99, "category": "STRATEGY" }
```

#### `UpdateEntryModel`

```json
{ "price": 12.49, "category": "STRATEGY" }
```

#### `PagedResponse<T>`

```json
{ "items": [ /* T */ ], "page": 0, "size": 10, "last": false }
```

#### `PostModel`

```json
{
  "id": "uuid",
  "title": "string",
  "image": "string",
  "type": "NEWS|UPDATE|...",
  "content": "string"
}
```

### 3.3 Orders

#### `OrderResponseModel`

```json
{
  "id": "uuid",
  "totalPrice": 12.34,
  "status": "CREATED|PAID|COMPLETED|...",
  "items": [
    { "gameId": "uuid", "price": 9.99 }
  ]
}
```

---

## 4) Checkout & payment flow (Mollie)

De Store-service gebruikt een payment provider adapter (Mollie) om een checkout URL te genereren.

**Aanbevolen flow voor clients (frontend):**

1. Voeg één of meerdere games toe aan cart via `/api/cart`.
2. Start checkout via `POST /api/orders`.
3. Redirect de user naar de teruggegeven **checkout URL**.
4. Na betaling (redirect of polling), laat de frontend de order finaliseren via `POST /api/orders/{orderId}/verify`.
5. Toon order-status via `GET /api/orders/{orderId}`.


---

## 5) Messaging (RabbitMQ)

### 5.1 Uitgaande events

De Store-service publiceert events via `RabbitTemplate` op de store-exchange (`RabbitMQTopology.EXCHANGE_STORE`).

#### Event: `OrderCompletedEvent`

- **Routing key (platform):** `store.order.completed`
- **Payload**

```json
{
  "userId": "uuid",
  "orderId": "uuid",
  "totalAmount": 12.34
}
```

#### Event: `GamesPurchasedEvent`

- **Routing key:** `RabbitMQTopology.ROUTING_KEY_GAMES_PURCHASED` (verwacht: `store.games.purchased`)
- **Payload**

```json
{
  "userId": "uuid",
  "gameIds": ["uuid", "uuid"]
}
```

### 5.2 Contract-afspraken voor consumers

- Payloads worden als JSON verstuurd (via Spring AMQP converter-config).
- Voeg nieuwe velden **backwards compatible** toe (consumers mogen onbekende velden negeren).

---

## 6) Security

### 6.1 CORS

- Allowed origins via `cors.allowed-origins`.
- Methods: `GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD`
- Headers: `*`
- Credentials: toegestaan

### 6.2 Authorization rules (zoals geconfigureerd)

- `/api/cart/**` → authenticated
- `/api/store/games` → authenticated
- `/api/store/games/**` → permitAll
- `/actuator/health/**` → permitAll
- `anyRequest()` → permitAll

**Gevolgen voor integratie**

- Voor cart en order-create heb je in praktijk een geldige `Authorization: Bearer <jwt>` nodig.
- Let op dat sommige catalogus CRUD endpoints door de huidige config publiek lijken; als je dit niet wil, pas `SecurityConfig` aan (bv. admin-rollen of `authenticated()`).

---

## 7) Error handling & HTTP status codes

De service gebruikt een globale `@ControllerAdvice` die exceptions mapt naar status codes met een **plain-text body** (`String`), meestal `ex.getMessage()`.

### 404 NOT FOUND

Voor resources die niet bestaan (bv. cart niet gevonden, item niet gevonden, game niet gevonden).

### 400 BAD REQUEST

Business rules / invalid input, zoals:

- game bestaat al in cart
- game reeds in bezit
- cart is leeg
- payment nog niet compleet
- validation errors (`MethodArgumentNotValidException`)

### 502 BAD GATEWAY

Externe payment provider errors (`PaymentProviderException`).

### 503 SERVICE UNAVAILABLE

Afhankelijkheden down (`ServiceUnavailableException`).

### 500 INTERNAL SERVER ERROR

Onverwachte fouten.

---

## 8) Integratie-notities voor externe game developers

### 8.1 Een nieuw spel in de store zetten

1. **Registreer je game** in het platform (zorg dat je een stabiele `gameId: UUID` hebt).
2. Zorg dat je game-metadata beschikbaar is voor de Store-service (titel, beschrijving, afbeelding, …), zodat `getGameWithMetadata(gameId)` kan slagen.
3. Maak de store-entry aan met je `gameId`, `price` en `category` via `POST /api/store/games`.
4. (Optioneel) Voorzie posts/updates (read-only endpoints bestaan onder `/api/store/games/{id}/posts`).

### 8.2 Na aankoop: entitlements/unlock

- Subscribe op `store.order.completed` en/of `store.games.purchased` om:
  - games in de library te unlocken
  - achievements, stats of in-game entitlements te triggeren
  - notifications richting spelers te tonen (via Communication-service)

### 8.3 ACL (anti-corruption layer)

Voor bestaande games die al buiten het platform bestonden (zoals het schaakspel) kan een **ACL** nodig zijn om bestaande API’s/identiteiten te vertalen naar platform-contracten. Voor **nieuwe** externe games ligt de verantwoordelijkheid van integratie bij de developer: publiceer/consumeer events en lever metadata volgens de contracten hierboven.


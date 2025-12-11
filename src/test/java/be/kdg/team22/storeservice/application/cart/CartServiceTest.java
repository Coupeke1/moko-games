package be.kdg.team22.storeservice.application.cart;

import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartId;
import be.kdg.team22.storeservice.domain.cart.CartRepository;
import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.cart.exceptions.*;
import be.kdg.team22.storeservice.domain.catalog.GameId;
import be.kdg.team22.storeservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;
import be.kdg.team22.storeservice.infrastructure.user.ExternalUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    CartRepository repo;
    final UUID USER = UUID.randomUUID();
    final UserId userId = new UserId(USER);
    CartService service;
    final UUID GAME = UUID.randomUUID();
    Jwt jwt = Jwt.withTokenValue("test-token").header("alg", "none").claim("sub", "test-user").build();

    ExternalGamesRepository games;
    ExternalUserRepository users;
    GameMetadataResponse validMetadata;

    @BeforeEach
    void setup() {
        repo = mock(CartRepository.class);
        games = mock(ExternalGamesRepository.class);
        users = mock(ExternalUserRepository.class);

        service = new CartService(repo, games, users);

        validMetadata = new GameMetadataResponse(GAME, "name", "title", "desc", "image.png", Instant.now(), Instant.now());
    }

    @Test
    @DisplayName("getOrCreate → returns existing cart")
    void getOrCreate_existing() {
        Cart existing = new Cart(CartId.create(), UserId.from(USER));
        when(repo.findByUserId(USER)).thenReturn(Optional.of(existing));

        Cart result = service.getOrCreate(userId);

        assertSame(existing, result);
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("getOrCreate → creates new cart when none exist")
    void getOrCreate_new() {
        when(repo.findByUserId(USER)).thenReturn(Optional.empty());

        Cart result = service.getOrCreate(userId);

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(repo).save(captor.capture());

        Cart saved = captor.getValue();

        assertEquals(USER, saved.userId().value());
        assertSame(saved, result);
    }

    @Test
    @DisplayName("get → returns existing cart")
    void get_existing() {
        Cart cart = new Cart(CartId.create(), UserId.from(USER));
        when(repo.findByUserId(USER)).thenReturn(Optional.of(cart));

        assertSame(cart, service.get(userId));
    }

    @Test
    @DisplayName("get → throws CartNotFound when not exist")
    void get_notFound() {
        when(repo.findByUserId(USER)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> service.get(userId));
    }

    @Test
    @DisplayName("addItem → throws metadata null")
    void addEntry_metadataNull() {
        when(games.fetchMetadata(GameId.from(GAME))).thenReturn(null);

        assertThrows(InvalidMetadataException.class, () -> service.addEntry(userId, GameId.from(GAME), jwt));
    }

    @Test
    @DisplayName("addItem → missing title")
    void addEntry_missingTitle() {
        GameMetadataResponse meta = new GameMetadataResponse(GAME, "name", "", "desc", "image", Instant.now(), Instant.now());
        when(games.fetchMetadata(GameId.from(GAME))).thenReturn(meta);

        assertThrows(InvalidMetadataException.class, () -> service.addEntry(userId, GameId.from(GAME), jwt));
    }

    @Test
    @DisplayName("addItem → missing image")
    void addEntry_missingImage() {
        GameMetadataResponse meta = new GameMetadataResponse(GAME, "name", "title", "desc", "", Instant.now(), Instant.now());
        when(games.fetchMetadata(GameId.from(GAME))).thenReturn(meta);

        assertThrows(InvalidMetadataException.class, () -> service.addEntry(userId, GameId.from(GAME), jwt));
    }

    @Test
    @DisplayName("addItem → user already owns game")
    void addEntry_userOwnsGame() {
        when(games.fetchMetadata(GameId.from(GAME))).thenReturn(validMetadata);
        when(users.userOwnsGame(GameId.from(GAME), jwt)).thenReturn(true);

        assertThrows(GameAlreadyOwnedException.class, () -> service.addEntry(userId, GameId.from(GAME), jwt));
    }

    @Test
    @DisplayName("addItem → adds to existing cart")
    void addEntry_existingCart() {
        Cart cart = new Cart(CartId.create(), UserId.from(USER));
        when(games.fetchMetadata(GameId.from(GAME))).thenReturn(validMetadata);
        when(users.userOwnsGame(GameId.from(GAME), jwt)).thenReturn(false);
        when(repo.findByUserId(USER)).thenReturn(Optional.of(cart));

        service.addEntry(userId, GameId.from(GAME), jwt);

        assertTrue(cart.entries().stream().anyMatch(i -> i.gameId().value().equals(GAME)));
        verify(repo).save(cart);
    }

    @Test
    @DisplayName("addItem → creates new cart when none exists")
    void addEntry_newCart() {
        when(games.fetchMetadata(GameId.from(GAME))).thenReturn(validMetadata);
        when(users.userOwnsGame(GameId.from(GAME), jwt)).thenReturn(false);
        when(repo.findByUserId(USER)).thenReturn(Optional.empty());

        service.addEntry(userId, GameId.from(GAME), jwt);

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(repo, atLeastOnce()).save(captor.capture());

        Cart saved = captor.getValue();
        assertEquals(USER, saved.userId().value());
        assertTrue(saved.entries().stream().anyMatch(i -> i.gameId().value().equals(GAME)));
    }

    @Test
    @DisplayName("removeItem → happy path")
    void removeEntry() {
        Cart cart = new Cart(CartId.create(), UserId.from(USER));
        cart.add(GameId.from(GAME));

        when(repo.findByUserId(USER)).thenReturn(Optional.of(cart));

        service.removeEntry(userId, GameId.from(GAME));

        assertTrue(cart.entries().isEmpty());
        verify(repo).save(cart);
    }

    @Test
    @DisplayName("removeItem → item not found")
    void removeEntry_notFound() {
        Cart cart = new Cart(CartId.create(), UserId.from(USER));
        when(repo.findByUserId(USER)).thenReturn(Optional.of(cart));

        assertThrows(CartItemNotFoundException.class, () -> service.removeEntry(userId, GameId.from(GAME)));
    }

    @Test
    @DisplayName("clearCart → happy path")
    void clear() {
        Cart cart = new Cart(CartId.create(), UserId.from(USER));
        cart.add(GameId.from(GAME));
        when(repo.findByUserId(USER)).thenReturn(Optional.of(cart));

        service.clear(userId);

        assertTrue(cart.entries().isEmpty());
        verify(repo).save(cart);
    }

    @Test
    @DisplayName("clearCart → empty cart throws CartEmptyException")
    void clear_empty() {
        Cart cart = new Cart(CartId.create(), UserId.from(USER));
        when(repo.findByUserId(USER)).thenReturn(Optional.of(cart));

        assertThrows(CartEmptyException.class, () -> service.clear(userId));
    }
}
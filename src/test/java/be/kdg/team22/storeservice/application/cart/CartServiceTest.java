package be.kdg.team22.storeservice.application.cart;

import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartId;
import be.kdg.team22.storeservice.domain.cart.CartRepository;
import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.cart.exceptions.*;
import be.kdg.team22.storeservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;
import be.kdg.team22.storeservice.infrastructure.user.ExternalUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
    final String JWT = "jwt-token";
    ExternalGamesRepository games;
    ExternalUserRepository users;
    GameMetadataResponse validMetadata;

    @BeforeEach
    void setup() {
        repo = mock(CartRepository.class);
        games = mock(ExternalGamesRepository.class);
        users = mock(ExternalUserRepository.class);

        service = new CartService(repo, games, users);

        validMetadata = new GameMetadataResponse(
                GAME,
                "name",
                "title",
                "desc",
                "image.png",
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    @DisplayName("getOrCreate → returns existing cart")
    void getOrCreate_existing() {
        Cart existing = new Cart(CartId.create(), USER);
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

        assertEquals(USER, saved.userId());
        assertSame(saved, result);
    }

    @Test
    @DisplayName("get → returns existing cart")
    void get_existing() {
        Cart cart = new Cart(CartId.create(), USER);
        when(repo.findByUserId(USER)).thenReturn(Optional.of(cart));

        assertSame(cart, service.get(userId));
    }

    @Test
    @DisplayName("get → throws CartNotFound when not exist")
    void get_notFound() {
        when(repo.findByUserId(USER)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class,
                () -> service.get(userId));
    }

    @Test
    @DisplayName("addItem → throws metadata null")
    void addItem_metadataNull() {
        when(games.fetchMetadata(GAME)).thenReturn(null);

        assertThrows(InvalidMetadataException.class,
                () -> service.addItem(userId, GAME, JWT));
    }

    @Test
    @DisplayName("addItem → missing title")
    void addItem_missingTitle() {
        GameMetadataResponse meta = new GameMetadataResponse(
                GAME, "name", "", "desc", "image", Instant.now(), Instant.now()
        );
        when(games.fetchMetadata(GAME)).thenReturn(meta);

        assertThrows(InvalidMetadataException.class,
                () -> service.addItem(userId, GAME, JWT));
    }

    @Test
    @DisplayName("addItem → missing image")
    void addItem_missingImage() {
        GameMetadataResponse meta = new GameMetadataResponse(
                GAME, "name", "title", "desc", "", Instant.now(), Instant.now()
        );
        when(games.fetchMetadata(GAME)).thenReturn(meta);

        assertThrows(InvalidMetadataException.class,
                () -> service.addItem(userId, GAME, JWT));
    }

    @Test
    @DisplayName("addItem → user already owns game")
    void addItem_userOwnsGame() {
        when(games.fetchMetadata(GAME)).thenReturn(validMetadata);
        when(users.userOwnsGame(GAME, JWT)).thenReturn(true);

        assertThrows(GameAlreadyOwnedException.class,
                () -> service.addItem(userId, GAME, JWT));
    }

    @Test
    @DisplayName("addItem → adds to existing cart")
    void addItem_existingCart() {
        Cart cart = new Cart(CartId.create(), USER);
        when(games.fetchMetadata(GAME)).thenReturn(validMetadata);
        when(users.userOwnsGame(GAME, JWT)).thenReturn(false);
        when(repo.findByUserId(USER)).thenReturn(Optional.of(cart));

        service.addItem(userId, GAME, JWT);

        assertTrue(cart.items().stream().anyMatch(i -> i.gameId().equals(GAME)));
        verify(repo).save(cart);
    }

    @Test
    @DisplayName("addItem → creates new cart when none exists")
    void addItem_newCart() {
        when(games.fetchMetadata(GAME)).thenReturn(validMetadata);
        when(users.userOwnsGame(GAME, JWT)).thenReturn(false);
        when(repo.findByUserId(USER)).thenReturn(Optional.empty());

        service.addItem(userId, GAME, JWT);

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(repo, atLeastOnce()).save(captor.capture());

        Cart saved = captor.getValue();
        assertEquals(USER, saved.userId());
        assertTrue(saved.items().stream().anyMatch(i -> i.gameId().equals(GAME)));
    }

    @Test
    @DisplayName("removeItem → happy path")
    void removeItem() {
        Cart cart = new Cart(CartId.create(), USER);
        cart.addItem(GAME);

        when(repo.findByUserId(USER)).thenReturn(Optional.of(cart));

        service.removeItem(userId, GAME);

        assertTrue(cart.items().isEmpty());
        verify(repo).save(cart);
    }

    @Test
    @DisplayName("removeItem → item not found")
    void removeItem_notFound() {
        Cart cart = new Cart(CartId.create(), USER);
        when(repo.findByUserId(USER)).thenReturn(Optional.of(cart));

        assertThrows(CartItemNotFoundException.class,
                () -> service.removeItem(userId, GAME));
    }

    @Test
    @DisplayName("clearCart → happy path")
    void clearCart() {
        Cart cart = new Cart(CartId.create(), USER);
        cart.addItem(GAME);
        when(repo.findByUserId(USER)).thenReturn(Optional.of(cart));

        service.clearCart(userId);

        assertTrue(cart.items().isEmpty());
        verify(repo).save(cart);
    }

    @Test
    @DisplayName("clearCart → empty cart throws CartEmptyException")
    void clearCart_empty() {
        Cart cart = new Cart(CartId.create(), USER);
        when(repo.findByUserId(USER)).thenReturn(Optional.of(cart));

        assertThrows(CartEmptyException.class,
                () -> service.clearCart(userId));
    }
}
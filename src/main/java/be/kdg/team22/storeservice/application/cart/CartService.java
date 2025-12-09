package be.kdg.team22.storeservice.application.cart;

import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartId;
import be.kdg.team22.storeservice.domain.cart.CartRepository;
import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.cart.exceptions.CartEmptyException;
import be.kdg.team22.storeservice.domain.cart.exceptions.CartNotFoundException;
import be.kdg.team22.storeservice.domain.cart.exceptions.GameAlreadyOwnedException;
import be.kdg.team22.storeservice.domain.cart.exceptions.InvalidMetadataException;
import be.kdg.team22.storeservice.domain.catalog.GameId;
import be.kdg.team22.storeservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;
import be.kdg.team22.storeservice.infrastructure.user.ExternalUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final ExternalGamesRepository gamesRepository;
    private final ExternalUserRepository userRepository;

    public CartService(final CartRepository cartRepository, final ExternalGamesRepository gamesRepository, final ExternalUserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.gamesRepository = gamesRepository;
        this.userRepository = userRepository;
    }

    public Cart getOrCreate(final UserId id) {
        return cartRepository.findByUserId(id.value()).orElseGet(() -> {
            Cart cart = new Cart(CartId.create(), id.value());
            cartRepository.save(cart);
            return cart;
        });
    }

    public Cart get(final UserId userId) {
        return cartRepository.findByUserId(userId.value()).orElseThrow(() -> new CartNotFoundException(userId.value()));
    }

    public void addItem(final UserId userId, final GameId gameId, final String jwt) {
        GameMetadataResponse meta = gamesRepository.fetchMetadata(gameId);
        validateMetadata(meta, gameId);

        if (userRepository.userOwnsGame(gameId.value(), jwt))
            throw new GameAlreadyOwnedException(gameId.value(), userId.value());

        Cart cart = cartRepository.findByUserId(userId.value()).orElseGet(() -> {
            Cart newCart = new Cart(new CartId(UUID.randomUUID()), userId.value());
            cartRepository.save(newCart);
            return newCart;
        });

        cart.addItem(gameId);
        cartRepository.save(cart);
    }

    public void removeItem(final UserId userId, final GameId gameId) {
        Cart cart = get(userId);
        cart.removeItem(gameId);
        cartRepository.save(cart);
    }

    public void clearCart(final UserId userId) {
        Cart cart = get(userId);

        if (cart.isEmpty())
            throw new CartEmptyException(userId.value());

        cart.clear();
        cartRepository.save(cart);
    }

    private void validateMetadata(GameMetadataResponse meta, GameId gameId) {
        if (meta == null)
            throw new InvalidMetadataException(gameId.value(), "Game metadata is null");

        if (meta.title() == null || meta.title().isBlank())
            throw new InvalidMetadataException(gameId.value(), "Title missing");

        if (meta.image() == null || meta.image().isBlank())
            throw new InvalidMetadataException(gameId.value(), "Image missing");
    }
}
package be.kdg.team22.storeservice.application.cart;

import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartId;
import be.kdg.team22.storeservice.domain.cart.CartRepository;
import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.cart.exceptions.CartEmptyException;
import be.kdg.team22.storeservice.domain.cart.exceptions.CartNotFoundException;
import be.kdg.team22.storeservice.domain.cart.exceptions.GameAlreadyOwnedException;
import be.kdg.team22.storeservice.domain.cart.exceptions.InvalidMetadataException;
import be.kdg.team22.storeservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;
import be.kdg.team22.storeservice.infrastructure.user.ExternalUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class CartService {

    private final CartRepository repo;
    private final ExternalGamesRepository gamesRepository;
    private final ExternalUserRepository userRepository;

    public CartService(CartRepository repo,
                       ExternalGamesRepository gamesRepository,
                       ExternalUserRepository userRepository) {
        this.repo = repo;
        this.gamesRepository = gamesRepository;
        this.userRepository = userRepository;
    }

    public Cart getOrCreate(final UserId userId) {
        return repo.findByUserId(userId.value())
                .orElseGet(() -> {
                    Cart c = new Cart(CartId.create(), userId.value());
                    repo.save(c);
                    return c;
                });
    }

    public Cart get(final UserId userId) {
        return repo.findByUserId(userId.value())
                .orElseThrow(() -> new CartNotFoundException(userId.value()));
    }

    public void addItem(final UserId userId, final UUID gameId, final String jwt) {
        GameMetadataResponse meta = gamesRepository.fetchMetadata(gameId);
        validateMetadata(meta, gameId);

        if (userRepository.userOwnsGame(gameId, jwt))
            throw new GameAlreadyOwnedException(gameId, userId.value());

        Cart cart = repo.findByUserId(userId.value())
                .orElseGet(() -> {
                    Cart newCart = new Cart(new CartId(UUID.randomUUID()), userId.value());
                    repo.save(newCart);
                    return newCart;
                });

        cart.addItem(gameId);
        repo.save(cart);
    }

    public void removeItem(final UserId userId, final UUID gameId) {
        Cart cart = get(userId);
        cart.removeItem(gameId);
        repo.save(cart);
    }

    public void clearCart(final UserId userId) {
        Cart cart = get(userId);

        if (cart.isEmpty()) throw new CartEmptyException(userId.value());

        cart.clear();
        repo.save(cart);
    }

    private void validateMetadata(GameMetadataResponse meta, UUID gameId) {
        if (meta == null)
            throw new InvalidMetadataException(gameId, "Game metadata is null");

        if (meta.title() == null || meta.title().isBlank())
            throw new InvalidMetadataException(gameId, "Title missing");

        if (meta.image() == null || meta.image().isBlank())
            throw new InvalidMetadataException(gameId, "Image missing");
    }
}
package be.kdg.team22.storeservice.application.cart;

import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartId;
import be.kdg.team22.storeservice.domain.cart.CartRepository;
import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.cart.exceptions.CartEmptyException;
import be.kdg.team22.storeservice.domain.cart.exceptions.CartNotFoundException;
import be.kdg.team22.storeservice.domain.cart.exceptions.GameAlreadyOwnedException;
import be.kdg.team22.storeservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.storeservice.infrastructure.user.ExternalUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CartService {

    private final CartRepository repo;
    private final ExternalGamesRepository gamesRepo;
    private final ExternalUserRepository userRepo;

    public CartService(final CartRepository repo,
                       final ExternalGamesRepository gamesRepo,
                       final ExternalUserRepository userRepo) {
        this.repo = repo;
        this.gamesRepo = gamesRepo;
        this.userRepo = userRepo;
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

        gamesRepo.fetchMetadata(gameId);

        boolean owns = userRepo.userOwnsGame(gameId, jwt);
        if (owns) throw new GameAlreadyOwnedException(gameId, userId.value());

        Cart cart = repo.findByUserId(userId.value())
                .orElseGet(() -> repo.save(new Cart(new CartId(UUID.randomUUID()), userId.value())));

        cart.addItem(gameId);
        repo.save(cart);
    }

    public void removeItem(UserId userId, UUID gameId) {
        Cart cart = get(userId);
        cart.removeItem(gameId);
        repo.save(cart);
    }

    public void clearCart(UserId userId) {
        Cart cart = get(userId);

        if (cart.isEmpty()) throw new CartEmptyException(userId.value());

        cart.clear();
        repo.save(cart);
    }
}

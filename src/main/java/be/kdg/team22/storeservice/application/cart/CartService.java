package be.kdg.team22.storeservice.application.cart;

import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartId;
import be.kdg.team22.storeservice.domain.cart.CartRepository;
import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.cart.exceptions.CartEmptyException;
import be.kdg.team22.storeservice.domain.cart.exceptions.CartNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CartService {

    private final CartRepository repo;

    public CartService(CartRepository repo) {
        this.repo = repo;
    }

    public Cart getOrCreate(UserId userId) {
        return repo.findByUserId(userId.value())
                .orElseGet(() -> {
                    Cart c = new Cart(CartId.create(), userId.value());
                    repo.save(c);
                    return c;
                });
    }

    public Cart get(UserId userId) {
        return repo.findByUserId(userId.value())
                .orElseThrow(() -> new CartNotFoundException(userId.value()));
    }

    public void addItem(UserId userId, UUID gameId) {
        Cart cart = getOrCreate(userId);
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

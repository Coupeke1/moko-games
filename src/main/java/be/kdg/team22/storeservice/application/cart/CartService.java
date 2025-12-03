package be.kdg.team22.storeservice.application.cart;

import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartId;
import be.kdg.team22.storeservice.domain.cart.CartRepository;
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

    public Cart getOrCreate(CartId userId) {
        return repo.findByUserId(userId.value())
                .orElseGet(() -> {
                    Cart c = new Cart(CartId.create(), userId.value());
                    repo.save(c);
                    return c;
                });
    }

    public Cart get(CartId userId) {
        return repo.findByUserId(userId.value())
                .orElseThrow(() -> new CartNotFoundException(userId.value()));
    }

    public void addItem(CartId userId, UUID gameId) {
        Cart cart = getOrCreate(userId);
        cart.addItem(gameId);
        repo.save(cart);
    }

    public void removeItem(CartId userId, UUID gameId) {
        Cart cart = get(userId);
        cart.removeItem(gameId);
        repo.save(cart);
    }

    public void clearCart(CartId userId) {
        Cart cart = get(userId);

        if (cart.isEmpty()) throw new CartEmptyException(userId.value());

        cart.clear();
        repo.save(cart);
    }
}

package be.kdg.team22.storeservice.application.cart;

import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartRepository;
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

    public Cart getOrCreate(UUID userId) {
        return repo.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart(userId);
                    repo.save(cart);
                    return cart;
                });
    }

    public Cart get(UUID userId) {
        return repo.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
    }

    public void addItem(UUID userId, UUID gameId, int quantity) {
        Cart cart = getOrCreate(userId);
        cart.addItem(gameId, quantity);
        repo.save(cart);
    }

    public void updateQuantity(UUID userId, UUID gameId, int qty) {
        Cart cart = get(userId);
        cart.updateQuantity(gameId, qty);
        repo.save(cart);
    }

    public void removeItem(UUID userId, UUID gameId) {
        Cart cart = get(userId);
        cart.removeItem(gameId);
        repo.save(cart);
    }

    public void clearCart(UUID userId) {
        Cart cart = get(userId);
        cart.clear();
        repo.save(cart);
    }
}
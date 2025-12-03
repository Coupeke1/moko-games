package be.kdg.team22.storeservice.application.cart;

import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartRepository;
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

    public Cart get(UUID userId) {
        return repo.findByUserId(userId)
                .orElseGet(() -> create(userId));
    }

    private Cart create(UUID userId) {
        Cart cart = new Cart(userId);
        repo.save(cart);
        return cart;
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
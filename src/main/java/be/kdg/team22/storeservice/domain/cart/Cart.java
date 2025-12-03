package be.kdg.team22.storeservice.domain.cart;

import be.kdg.team22.storeservice.domain.cart.exceptions.CartItemNotFoundException;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AggregateRoot
public class Cart {

    private final UUID userId;
    private final Map<UUID, CartItem> items;

    public Cart(UUID userId) {
        this.userId = userId;
        this.items = new HashMap<>();
    }

    public Cart(UUID userId, List<CartItem> existing) {
        this.userId = userId;
        this.items = new HashMap<>();
        existing.forEach(i -> items.put(i.gameId(), i));
    }

    public UUID userId() {
        return userId;
    }

    public List<CartItem> items() {
        return List.copyOf(items.values());
    }

    public void addItem(UUID gameId, int quantity) {
        CartItem merged = items.containsKey(gameId)
                ? items.get(gameId).add(quantity)
                : new CartItem(gameId, quantity);

        items.put(gameId, merged);
    }

    public void removeItem(UUID gameId) {
        if (!items.containsKey(gameId)) {
            throw new CartItemNotFoundException(gameId);
        }
        items.remove(gameId);
    }

    public void clear() {
        items.clear();
    }
}
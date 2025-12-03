package be.kdg.team22.storeservice.domain.cart;

import be.kdg.team22.storeservice.domain.cart.exceptions.CartItemNotFoundException;
import be.kdg.team22.storeservice.domain.cart.exceptions.CartItemQuantityException;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AggregateRoot
public class Cart {

    private final CartId id;
    private final UUID userId;

    private final Map<UUID, CartItem> items = new HashMap<>();

    public Cart(CartId id, UUID userId, List<CartItem> existing) {
        this.id = id;
        this.userId = userId;

        for (CartItem i : existing) {
            items.put(i.gameId(), i);
        }
    }

    public Cart(UUID userId) {
        this.id = CartId.create();
        this.userId = userId;
    }

    public void addItem(UUID gameId, int quantity) {
        if (quantity <= 0) throw new CartItemQuantityException(quantity);

        items.merge(
                gameId,
                new CartItem(gameId, quantity),
                (oldItem, newItem) -> oldItem.withAddedQuantity(quantity)
        );
    }

    public void updateQuantity(UUID gameId, int quantity) {
        if (quantity <= 0) throw new CartItemQuantityException(quantity);

        CartItem existing = items.get(gameId);
        if (existing == null)
            throw new CartItemNotFoundException(gameId);

        items.put(gameId, existing.withQuantity(quantity));
    }

    public void removeItem(UUID gameId) {
        if (!items.containsKey(gameId))
            throw new CartItemNotFoundException(gameId);

        items.remove(gameId);
    }

    public void clear() {
        items.clear();
    }

    public CartId id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public List<CartItem> items() {
        return List.copyOf(items.values());
    }

    public int totalQuantity() {
        return items.values().stream()
                .mapToInt(CartItem::quantity)
                .sum();
    }
}
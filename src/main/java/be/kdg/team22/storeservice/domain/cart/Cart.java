package be.kdg.team22.storeservice.domain.cart;

import be.kdg.team22.storeservice.domain.cart.exceptions.QuantityMustBePositiveException;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AggregateRoot
public class Cart {

    private final UUID userId;
    private final Map<UUID, CartItem> items = new HashMap<>();

    public Cart(UUID userId) {
        this.userId = userId;
    }

    public UUID userId() {
        return userId;
    }

    public Collection<CartItem> items() {
        return items.values();
    }

    public void addItem(UUID gameId, int quantity) {
        if (quantity <= 0)
            throw new QuantityMustBePositiveException(quantity);

        items.merge(
                gameId,
                new CartItem(gameId, quantity),
                (existing, added) -> new CartItem(existing.gameId(), existing.quantity() + added.quantity())
        );
    }

    public void removeItem(UUID gameId) {
        items.remove(gameId);
    }

    public void clear() {
        items.clear();
    }
}
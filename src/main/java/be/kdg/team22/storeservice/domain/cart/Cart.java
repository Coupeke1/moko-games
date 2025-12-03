package be.kdg.team22.storeservice.domain.cart;

import be.kdg.team22.storeservice.domain.cart.exceptions.CartItemNotFoundException;
import be.kdg.team22.storeservice.domain.cart.exceptions.GameAlreadyInCartException;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AggregateRoot
public class Cart {

    private final CartId id;
    private final UUID userId;
    private final Set<CartItem> items = new HashSet<>();

    public Cart(final CartId id, final UUID userId, final Set<CartItem> items) {
        this.id = id;
        this.userId = userId;
        this.items.addAll(items);
    }

    public Cart(final CartId id, final UUID userId) {
        this.id = id;
        this.userId = userId;
    }

    public CartId id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public Set<CartItem> items() {
        return Set.copyOf(items);
    }

    public void addItem(final UUID gameId) {
        CartItem item = new CartItem(gameId);

        if (items.contains(item)) throw new GameAlreadyInCartException(gameId, userId);

        items.add(item);
    }

    public void removeItem(final UUID gameId) {
        boolean removed = items.removeIf(i -> i.gameId().equals(gameId));

        if (!removed) throw new CartItemNotFoundException(gameId, userId);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
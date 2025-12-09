package be.kdg.team22.storeservice.domain.cart;

import be.kdg.team22.storeservice.domain.cart.exceptions.CartItemNotFoundException;
import be.kdg.team22.storeservice.domain.cart.exceptions.GameAlreadyInCartException;
import be.kdg.team22.storeservice.domain.catalog.GameId;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.HashSet;
import java.util.Set;

@AggregateRoot
public class Cart {
    private final CartId id;
    private final UserId userId;
    private final Set<CartItem> entries = new HashSet<>();

    public Cart(final CartId id, final UserId userId, final Set<CartItem> entries) {
        this.id = id;
        this.userId = userId;
        this.entries.addAll(entries);
    }

    public Cart(final CartId id, final UserId userId) {
        this.id = id;
        this.userId = userId;
    }

    public CartId id() {
        return id;
    }

    public UserId userId() {
        return userId;
    }

    public Set<CartItem> entries() {
        return Set.copyOf(entries);
    }

    public void add(final GameId gameId) {
        CartItem item = new CartItem(gameId);

        if (entries.contains(item))
            throw new GameAlreadyInCartException(gameId.value(), userId.value());

        entries.add(item);
    }

    public void remove(final GameId gameId) {
        boolean removed = entries.removeIf(i -> i.gameId().equals(gameId));

        if (!removed)
            throw new CartItemNotFoundException(gameId.value(), userId.value());
    }

    public void clear() {
        entries.clear();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }
}
package be.kdg.team22.storeservice.domain.cart;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record CartItem(
        UUID gameId,
        int quantity
) {
    public CartItem withAddedQuantity(int extra) {
        return new CartItem(gameId, quantity + extra);
    }

    public CartItem withQuantity(int newQuantity) {
        return new CartItem(gameId, newQuantity);
    }
}
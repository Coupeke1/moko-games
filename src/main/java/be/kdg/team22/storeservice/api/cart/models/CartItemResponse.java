package be.kdg.team22.storeservice.api.cart.models;

import be.kdg.team22.storeservice.domain.cart.CartItem;

import java.util.UUID;

public record CartItemResponse(
        UUID gameId,
        int quantity
) {
    public static CartItemResponse from(CartItem item) {
        return new CartItemResponse(
                item.gameId(),
                item.quantity()
        );
    }
}
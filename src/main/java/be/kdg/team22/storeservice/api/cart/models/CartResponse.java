package be.kdg.team22.storeservice.api.cart.models;

import be.kdg.team22.storeservice.domain.cart.Cart;

import java.util.List;
import java.util.UUID;

public record CartResponse(
        UUID userId,
        List<CartItemResponse> items
) {
    public static CartResponse from(Cart cart) {
        return new CartResponse(
                cart.userId(),
                cart.items().stream()
                        .map(CartItemResponse::from)
                        .toList()
        );
    }
}

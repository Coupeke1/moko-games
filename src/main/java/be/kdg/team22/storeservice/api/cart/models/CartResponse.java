package be.kdg.team22.storeservice.api.cart.models;

import be.kdg.team22.storeservice.domain.cart.Cart;

import java.util.List;

public record CartResponse(
        List<CartItemResponse> items,
        int totalQuantity
) {
    public static CartResponse from(Cart cart) {
        var dtoItems = cart.items().stream()
                .map(CartItemResponse::from)
                .toList();

        return new CartResponse(dtoItems, cart.totalQuantity());
    }
}
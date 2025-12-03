package be.kdg.team22.storeservice.api.cart.models;

import be.kdg.team22.storeservice.domain.cart.CartItem;

import java.util.UUID;

public record CartItemResponseModel(
        UUID gameId
) {
    public static CartItemResponseModel from(CartItem item) {
        return new CartItemResponseModel(item.gameId());
    }
}
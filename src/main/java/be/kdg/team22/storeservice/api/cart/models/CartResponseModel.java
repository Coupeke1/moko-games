package be.kdg.team22.storeservice.api.cart.models;

import be.kdg.team22.storeservice.domain.cart.Cart;

import java.util.List;

public record CartResponseModel(
        List<CartItemResponseModel> items
) {
    public static CartResponseModel from(Cart cart) {
        var dtoItems = cart.items().stream()
                .map(CartItemResponseModel::from)
                .toList();

        return new CartResponseModel(dtoItems);
    }
}
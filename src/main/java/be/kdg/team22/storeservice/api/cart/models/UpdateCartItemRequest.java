package be.kdg.team22.storeservice.api.cart.models;

import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequest(
        @Positive int quantity
) {
}

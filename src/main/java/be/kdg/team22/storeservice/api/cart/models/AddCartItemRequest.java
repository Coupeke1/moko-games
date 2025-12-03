package be.kdg.team22.storeservice.api.cart.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record AddCartItemRequest(
        @NotNull UUID gameId,
        @Positive int quantity
) {
}


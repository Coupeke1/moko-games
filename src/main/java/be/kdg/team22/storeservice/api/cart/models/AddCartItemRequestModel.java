package be.kdg.team22.storeservice.api.cart.models;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddCartItemRequestModel(
        @NotNull UUID gameId
) {
}
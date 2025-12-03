package be.kdg.team22.storeservice.domain.cart;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record CartItem(
        UUID gameId,
        int quantity
) {
}
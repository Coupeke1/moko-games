package be.kdg.team22.storeservice.api.cart.models;

import be.kdg.team22.storeservice.domain.cart.exceptions.GameIdCannotBeNullException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record CartItemModel(UUID gameId) {

    public CartItemModel {
        if (gameId == null)
            throw new GameIdCannotBeNullException();
    }
}

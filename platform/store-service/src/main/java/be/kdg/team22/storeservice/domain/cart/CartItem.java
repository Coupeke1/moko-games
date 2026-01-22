package be.kdg.team22.storeservice.domain.cart;

import be.kdg.team22.storeservice.domain.catalog.GameId;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record CartItem(
        GameId gameId
) {
}
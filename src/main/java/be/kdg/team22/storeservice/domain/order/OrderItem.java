package be.kdg.team22.storeservice.domain.order;

import be.kdg.team22.storeservice.domain.catalog.GameId;
import org.jmolecules.ddd.annotation.ValueObject;

import java.math.BigDecimal;

@ValueObject
public record OrderItem(GameId gameId, BigDecimal price) {
}

package be.kdg.team22.storeservice.domain.order;

import org.jmolecules.ddd.annotation.ValueObject;

import java.math.BigDecimal;
import java.util.UUID;

@ValueObject
public record OrderItem(UUID gameId, BigDecimal price) {
}

package be.kdg.team22.storeservice.domain.order;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record OrderId(UUID value) {

    public static OrderId create() {
        return new OrderId(UUID.randomUUID());
    }
}
package be.kdg.team22.storeservice.domain.order;

import be.kdg.team22.storeservice.domain.order.exceptions.OrderNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record OrderId(UUID value) {
    public static OrderId create() {
        return new OrderId(UUID.randomUUID());
    }

    public static OrderId from(UUID value) {
        return new OrderId(value);
    }

    public OrderNotFoundException notFound() {
        return new OrderNotFoundException();
    }
}
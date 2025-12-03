package be.kdg.team22.storeservice.domain.cart;

import be.kdg.team22.storeservice.domain.cart.exceptions.CartIdCannotBeNullException;
import be.kdg.team22.storeservice.domain.cart.exceptions.CartNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record CartId(UUID value) {

    public CartId {
        if (value == null) {
            throw new CartIdCannotBeNullException();
        }
    }

    public static CartId from(UUID value) {
        return new CartId(value);
    }

    public static CartId create() {
        return new CartId(UUID.randomUUID());
    }

    public CartNotFoundException notFound() {
        return new CartNotFoundException(value);
    }
}
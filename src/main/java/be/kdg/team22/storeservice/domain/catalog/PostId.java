package be.kdg.team22.storeservice.domain.catalog;

import be.kdg.team22.storeservice.domain.catalog.exceptions.PostNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record PostId(UUID value) {
    public static PostId create() {
        return new PostId(UUID.randomUUID());
    }

    public static PostId from(UUID value) {
        return new PostId(value);
    }

    public PostNotFoundException notFound() {
        return new PostNotFoundException(this);
    }
}
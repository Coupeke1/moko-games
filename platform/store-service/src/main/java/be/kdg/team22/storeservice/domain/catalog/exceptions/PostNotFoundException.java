package be.kdg.team22.storeservice.domain.catalog.exceptions;

import be.kdg.team22.storeservice.domain.catalog.PostId;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(final PostId id) {
        super("Post with id '%s' not found".formatted(id));
    }
}

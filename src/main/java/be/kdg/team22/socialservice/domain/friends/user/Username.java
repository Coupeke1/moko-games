package be.kdg.team22.socialservice.domain.friends.user;

import be.kdg.team22.socialservice.domain.friends.user.exceptions.NotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Username(String value) {
    public static Username create(String value) {
        return new Username(value.trim());
    }

    public NotFoundException notFound() {
        throw new NotFoundException(value);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value.trim();
    }
}
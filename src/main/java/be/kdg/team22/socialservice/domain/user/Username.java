package be.kdg.team22.socialservice.domain.user;

import be.kdg.team22.socialservice.domain.user.exceptions.UserNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Username(String value) {
    public static Username create(String value) {
        return new Username(value.trim());
    }

    public UserNotFoundException notFound() {
        throw new UserNotFoundException(value);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value.trim();
    }
}
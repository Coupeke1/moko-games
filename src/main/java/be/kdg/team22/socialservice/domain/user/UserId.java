package be.kdg.team22.socialservice.domain.user;

import be.kdg.team22.socialservice.domain.user.exceptions.NotAuthenticatedException;
import be.kdg.team22.socialservice.domain.user.exceptions.UserNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@ValueObject
public record UserId(UUID value) {
    public UserId {
        if (value == null)
            throw new IllegalArgumentException("ID cannot be null");
    }

    public static UserId create(String value) {
        return new UserId(UUID.fromString(value));
    }

    public static UserId from(UUID value) {
        return new UserId(value);
    }

    public static UserId get(Jwt token) {
        String sub = token.getClaimAsString(StandardClaimNames.SUB);
        if (sub == null)
            throw new NotAuthenticatedException();

        return create(sub);
    }

    public UserNotFoundException notFound() {
        throw new UserNotFoundException(this);
    }
}
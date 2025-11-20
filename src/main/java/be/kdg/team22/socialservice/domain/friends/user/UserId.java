package be.kdg.team22.socialservice.domain.friends.user;

import be.kdg.team22.socialservice.domain.friends.user.exceptions.NotAuthenticatedException;
import be.kdg.team22.socialservice.domain.friends.user.exceptions.NotFoundException;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public record UserId(UUID value) {
    public static UserId create(String value) {
        return new UserId(UUID.fromString(value));
    }

    public static UserId from(UUID uuid) {
        return new UserId(uuid);
    }

    public static UserId get(Jwt token) {
        String sub = token.getClaimAsString(StandardClaimNames.SUB);
        if (sub == null)
            throw new NotAuthenticatedException();

        return create(sub);
    }

    public NotFoundException notFound() {
        throw new NotFoundException(value);
    }
}
package be.kdg.team22.socialservice.friends.domain;

import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public record UserId(UUID value) {
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
    }

    public static UserId from(UUID uuid) {
        return new UserId(uuid);
    }

    public static UserId fromString(String value) {
        return new UserId(UUID.fromString(value));
    }

    public static UserId fromJwt(Jwt jwt) {
        String sub = jwt.getClaimAsString(StandardClaimNames.SUB);
        if (sub == null) {
            throw new IllegalArgumentException("JWT does not contain subject (sub) claim");
        }
        return fromString(sub);
    }
}

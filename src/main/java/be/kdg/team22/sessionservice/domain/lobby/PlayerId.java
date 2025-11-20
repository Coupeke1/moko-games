package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.ClaimNotFoundException;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public record PlayerId(UUID value) {
    public PlayerId {
        if (value == null)
            throw new IllegalArgumentException("PlayerId cannot be null");
    }

    public static PlayerId from(UUID uuid) {
        return new PlayerId(uuid);
    }

    public static PlayerId create(String value) {
        return new PlayerId(UUID.fromString(value));
    }

    public static PlayerId get(Jwt token) {
        String sub = token.getClaimAsString(StandardClaimNames.SUB);
        if (sub == null)
            throw new ClaimNotFoundException("sub");

        return create(sub);
    }
}
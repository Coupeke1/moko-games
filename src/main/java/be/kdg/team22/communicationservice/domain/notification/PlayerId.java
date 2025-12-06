package be.kdg.team22.communicationservice.domain.notification;

import be.kdg.team22.communicationservice.domain.notification.exceptions.ClaimNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@ValueObject
public record PlayerId(UUID value) {
    public PlayerId(UUID value) {
        this.value = value;
    }

    public static PlayerId from(final UUID value) {
        return new PlayerId(value);
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
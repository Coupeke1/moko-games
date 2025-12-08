package be.kdg.team22.checkersservice.domain.player;

import be.kdg.team22.checkersservice.domain.player.exceptions.ClaimNotFoundException;
import be.kdg.team22.checkersservice.domain.player.exceptions.InvalidPlayerException;
import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@ValueObject
public record PlayerId(UUID value) {
    public PlayerId {
        if (value == null) throw new InvalidPlayerException();
    }

    public static PlayerId get(Jwt token) {
        String sub = token.getClaimAsString(StandardClaimNames.SUB);
        if (sub == null) throw ClaimNotFoundException.sub();

        return create(sub);
    }

    public static PlayerId create() {
        return new PlayerId(UUID.randomUUID());
    }

    public static PlayerId create(String value) {
        return new PlayerId(UUID.fromString(value));
    }
}
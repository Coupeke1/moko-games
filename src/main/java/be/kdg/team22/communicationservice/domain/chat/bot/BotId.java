package be.kdg.team22.communicationservice.domain.chat.bot;

import be.kdg.team22.communicationservice.domain.notification.exceptions.ClaimNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@ValueObject
public record BotId(UUID value) {

    public static BotId from(final UUID value) {
        return new BotId(value);
    }

    public static BotId create(String value) {
        return new BotId(UUID.fromString(value));
    }

    public static BotId create() {
        return new BotId(UUID.randomUUID());
    }

    public static BotId get(Jwt token) {
        String sub = token.getClaimAsString(StandardClaimNames.SUB);
        if (sub == null)
            throw new ClaimNotFoundException("sub");

        return create(sub);
    }
}
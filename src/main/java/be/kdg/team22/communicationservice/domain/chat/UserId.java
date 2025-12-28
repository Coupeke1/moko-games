package be.kdg.team22.communicationservice.domain.chat;

import be.kdg.team22.communicationservice.domain.chat.bot.BotId;
import be.kdg.team22.communicationservice.domain.chat.channel.exceptions.UserNotFoundException;
import be.kdg.team22.communicationservice.domain.notification.exceptions.ClaimNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@ValueObject
public record UserId(UUID value) {
    public UserId(UUID value) {
        this.value = value;
    }

    public static UserId from(final UUID value) {
        return new UserId(value);
    }

    public static UserId from(final BotId id) {
        return new UserId(id.value());
    }

    public static UserId create(String value) {
        return new UserId(UUID.fromString(value));
    }

    public static UserId create() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId get(Jwt token) {
        String sub = token.getClaimAsString(StandardClaimNames.SUB);
        if (sub == null)
            throw new ClaimNotFoundException("sub");

        return create(sub);
    }

    public UserNotFoundException notFound() {
        return new UserNotFoundException(this);
    }
}
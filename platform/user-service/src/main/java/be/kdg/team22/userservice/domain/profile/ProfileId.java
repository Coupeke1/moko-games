package be.kdg.team22.userservice.domain.profile;

import be.kdg.team22.userservice.domain.profile.exceptions.ClaimNotFoundException;
import be.kdg.team22.userservice.domain.profile.exceptions.NotAuthenticatedException;
import be.kdg.team22.userservice.domain.profile.exceptions.ProfileNotFoundException;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public record ProfileId(UUID value) {
    public static ProfileId create(String value) {
        return new ProfileId(UUID.fromString(value));
    }

    public static ProfileId create() {
        return new ProfileId(UUID.randomUUID());
    }

    public static ProfileId from(UUID id) {
        return new ProfileId(id);
    }

    public static ProfileId from(BotId id) {
        return new ProfileId(id.value());
    }

    public static ProfileId get(Jwt token) {
        if (token == null)
            throw new NotAuthenticatedException();
        String sub = token.getClaimAsString(StandardClaimNames.SUB);
        if (sub == null)
            throw ClaimNotFoundException.sub();

        return create(sub);
    }

    public ProfileNotFoundException notFound() {
        throw new ProfileNotFoundException(this);
    }
}
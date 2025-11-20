package be.kdg.team22.userservice.domain.profile;

import be.kdg.team22.userservice.domain.profile.exceptions.ClaimNotFoundException;
import be.kdg.team22.userservice.domain.profile.exceptions.NotFoundException;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public record ProfileId(UUID value) {
    public static ProfileId create(String value) {
        return new ProfileId(UUID.fromString(value));
    }

    public static ProfileId from(UUID uuid) {
        return new ProfileId(uuid);
    }

    public static ProfileId get(Jwt token) {
        String sub = token.getClaimAsString(StandardClaimNames.SUB);
        if (sub == null)
            throw new ClaimNotFoundException("sub");

        return create(sub);
    }

    public NotFoundException notFound() {
        throw new NotFoundException(value);
    }
}
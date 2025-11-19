package be.kdg.team22.userservice.profile.domain.profile;

import be.kdg.team22.userservice.profile.application.profile.MissingRequiredClaimException;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public record ProfileId(UUID value) {
    public ProfileId {
        if (value == null) throw new IllegalArgumentException("ProfileId cannot be null");
    }

    public static ProfileId create(String value) {
        return new ProfileId(UUID.fromString(value));
    }

    public static ProfileId from(UUID uuid) {
        return new ProfileId(uuid);
    }

    public static ProfileId get(Jwt token) {
        String sub = token.getClaimAsString(StandardClaimNames.SUB);
        if (sub == null) {
            throw new MissingRequiredClaimException("Missing claim: sub");
        }
        return create(sub);
    }
}

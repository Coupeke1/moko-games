package be.kdg.team22.sessionservice.domain.friends;

import be.kdg.team22.sessionservice.domain.friends.exceptions.FriendNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.ClaimNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@ValueObject
public record FriendId(UUID value) {
    public FriendId {
        if (value == null)
            throw new IllegalArgumentException("ID cannot be null");
    }

    public static FriendId from(UUID value) {
        return new FriendId(value);
    }

    public static FriendId create() {
        return new FriendId(UUID.randomUUID());
    }

    public static FriendId create(String value) {
        return new FriendId(UUID.fromString(value));
    }

    public static FriendId get(Jwt token) {
        String sub = token.getClaimAsString(StandardClaimNames.SUB);
        if (sub == null)
            throw new ClaimNotFoundException("sub");

        return create(sub);
    }

    public FriendNotFoundException notFound() {
        throw new FriendNotFoundException(this);
    }
}
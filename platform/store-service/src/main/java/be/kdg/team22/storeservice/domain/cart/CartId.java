package be.kdg.team22.storeservice.domain.cart;

import be.kdg.team22.storeservice.domain.cart.exceptions.CartIdCannotBeNullException;
import be.kdg.team22.storeservice.domain.cart.exceptions.CartNotFoundException;
import be.kdg.team22.storeservice.domain.cart.exceptions.ClaimNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@ValueObject
public record CartId(UUID value) {

    public CartId {
        if (value == null) {
            throw new CartIdCannotBeNullException();
        }
    }

    public static CartId from(final UUID value) {
        return new CartId(value);
    }

    public static CartId create() {
        return new CartId(UUID.randomUUID());
    }

    public static CartId create(final String value) {
        return new CartId(UUID.fromString(value));
    }

    public static CartId get(final Jwt token) {
        String sub = token.getClaimAsString(StandardClaimNames.SUB);
        if (sub == null)
            throw new ClaimNotFoundException("sub");

        return create(sub);
    }

    public CartNotFoundException notFound() {
        return new CartNotFoundException(value);
    }
}
package be.kdg.team22.storeservice.domain.cart;

import be.kdg.team22.storeservice.domain.cart.exceptions.CartIdCannotBeNullException;
import be.kdg.team22.storeservice.domain.cart.exceptions.CartNotFoundException;
import be.kdg.team22.storeservice.domain.cart.exceptions.ClaimNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@ValueObject
public record UserId(UUID value) {

    public UserId {
        if (value == null) {
            throw new CartIdCannotBeNullException();
        }
    }

    public static UserId from(UUID value) {
        return new UserId(value);
    }

    public static UserId create() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId create(String value) {
        return new UserId(UUID.fromString(value));
    }

    public static UserId get(Jwt token) {
        String sub = token.getClaimAsString(StandardClaimNames.SUB);
        if (sub == null)
            throw new ClaimNotFoundException("sub");

        return create(sub);
    }

    public CartNotFoundException notFound() {
        return new CartNotFoundException(value);
    }
}
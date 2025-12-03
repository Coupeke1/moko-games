package be.kdg.team22.storeservice.api.cart;

import be.kdg.team22.storeservice.api.cart.models.CartResponse;
import be.kdg.team22.storeservice.application.cart.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping
    public CartResponse get(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return CartResponse.from(service.get(userId));
    }

    @DeleteMapping("/items/{gameId}")
    public void removeItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID gameId
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        service.removeItem(userId, gameId);
    }

    @DeleteMapping
    public void clear(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        service.clearCart(userId);
    }
}
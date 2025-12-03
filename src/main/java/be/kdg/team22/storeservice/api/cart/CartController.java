package be.kdg.team22.storeservice.api.cart;

import be.kdg.team22.storeservice.api.cart.models.CartResponse;
import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.domain.cart.Cart;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CartResponse> get(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Cart cart = service.get(userId);
        return ResponseEntity.ok(CartResponse.from(cart));
    }

    @DeleteMapping("/items/{gameId}")
    public ResponseEntity<Void> removeItem(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID gameId) {
        service.removeItem(UUID.fromString(jwt.getSubject()), gameId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@AuthenticationPrincipal Jwt jwt) {
        service.clearCart(UUID.fromString(jwt.getSubject()));
        return ResponseEntity.noContent().build();
    }
}
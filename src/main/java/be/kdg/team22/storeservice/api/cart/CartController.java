package be.kdg.team22.storeservice.api.cart;

import be.kdg.team22.storeservice.api.cart.models.AddCartItemRequest;
import be.kdg.team22.storeservice.api.cart.models.CartResponse;
import be.kdg.team22.storeservice.api.cart.models.UpdateCartItemRequest;
import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.domain.cart.Cart;
import jakarta.validation.Valid;
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
    public ResponseEntity<CartResponse> getOrCreate(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Cart cart = service.getOrCreate(userId);
        return ResponseEntity.ok(CartResponse.from(cart));
    }

    @DeleteMapping("/items/{gameId}")
    public ResponseEntity<Void> removeItem(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID gameId) {
        UUID userId = UUID.fromString(jwt.getSubject());
        service.removeItem(userId, gameId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        service.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addItem(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        service.addItem(userId, request.gameId(), request.quantity());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/items/{gameId}")
    public ResponseEntity<Void> updateQuantity(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID gameId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        service.updateQuantity(userId, gameId, request.quantity());
        return ResponseEntity.noContent().build();
    }
}
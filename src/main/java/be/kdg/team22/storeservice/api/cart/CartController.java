package be.kdg.team22.storeservice.api.cart;


import be.kdg.team22.storeservice.api.cart.models.AddCartItemRequestModel;
import be.kdg.team22.storeservice.api.cart.models.CartResponseModel;
import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.UserId;
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

    public CartController(final CartService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<CartResponseModel> getOrCreate(@AuthenticationPrincipal final Jwt jwt) {
        UserId userId = UserId.get(jwt);
        Cart cart = service.getOrCreate(userId);
        return ResponseEntity.ok(CartResponseModel.from(cart));
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addItem(
            @AuthenticationPrincipal final Jwt jwt,
            @Valid @RequestBody final AddCartItemRequestModel request
    ) {
        UserId userId = UserId.get(jwt);
        service.addItem(userId, request.gameId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{gameId}")
    public ResponseEntity<Void> removeItem(
            @AuthenticationPrincipal final Jwt jwt,
            @PathVariable final UUID gameId
    ) {
        UserId userId = UserId.get(jwt);
        service.removeItem(userId, gameId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@AuthenticationPrincipal final Jwt jwt) {
        UserId userId = UserId.get(jwt);
        service.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
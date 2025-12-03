package be.kdg.team22.storeservice.api.cart;


import be.kdg.team22.storeservice.api.cart.models.AddCartItemRequestModel;
import be.kdg.team22.storeservice.api.cart.models.CartResponseModel;
import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartId;
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
    public ResponseEntity<CartResponseModel> getOrCreate(@AuthenticationPrincipal Jwt jwt) {
        CartId userId = CartId.get(jwt);
        Cart cart = service.getOrCreate(userId);
        return ResponseEntity.ok(CartResponseModel.from(cart));
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addItem(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AddCartItemRequestModel request
    ) {
        CartId userId = CartId.get(jwt);
        service.addItem(userId, request.gameId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{gameId}")
    public ResponseEntity<Void> removeItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID gameId
    ) {
        CartId userId = CartId.get(jwt);
        service.removeItem(userId, gameId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@AuthenticationPrincipal Jwt jwt) {
        CartId userId = CartId.get(jwt);
        service.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
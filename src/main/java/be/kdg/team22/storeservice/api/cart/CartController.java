package be.kdg.team22.storeservice.api.cart;


import be.kdg.team22.storeservice.api.cart.models.AddEntryModel;
import be.kdg.team22.storeservice.api.catalog.models.EntryModel;
import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.application.catalog.GameService;
import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.catalog.GameId;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {
    private final CartService cartService;
    private final GameService gameService;

    public CartController(final CartService cartService, final GameService gameService) {
        this.cartService = cartService;
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<Collection<EntryModel>> getOrCreate(@AuthenticationPrincipal final Jwt jwt) {
        UserId userId = UserId.get(jwt);
        Cart cart = cartService.getOrCreate(userId);
        Collection<EntryModel> models = getEntries(cart);
        return ResponseEntity.ok(models);
    }

    private List<EntryModel> getEntries(final Cart cart) {
        return cart.items().stream().map(item -> gameService.getGameWithMetadata(item.gameId())).toList();
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addItem(@AuthenticationPrincipal final Jwt jwt, @Valid @RequestBody final AddEntryModel request) {
        UserId userId = UserId.get(jwt);
        cartService.addItem(userId, GameId.from(request.id()), jwt.getTokenValue());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{gameId}")
    public ResponseEntity<Void> removeItem(@AuthenticationPrincipal final Jwt jwt, @PathVariable final UUID gameId) {
        UserId userId = UserId.get(jwt);
        cartService.removeItem(userId, GameId.from(gameId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@AuthenticationPrincipal final Jwt jwt) {
        UserId userId = UserId.get(jwt);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
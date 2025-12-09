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
    public ResponseEntity<Collection<EntryModel>> getOrCreate(@AuthenticationPrincipal final Jwt token) {
        UserId userId = UserId.get(token);
        Cart cart = cartService.getOrCreate(userId);
        Collection<EntryModel> models = getEntries(cart);
        return ResponseEntity.ok(models);
    }

    private List<EntryModel> getEntries(final Cart cart) {
        return cart.entries().stream().map(item -> gameService.getGameWithMetadata(item.gameId())).toList();
    }

    @PostMapping("/entries")
    public ResponseEntity<Void> addEntry(@AuthenticationPrincipal final Jwt token, @Valid @RequestBody final AddEntryModel request) {
        UserId userId = UserId.get(token);
        cartService.addEntry(userId, GameId.from(request.id()), token.getTokenValue());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/entries/{id}")
    public ResponseEntity<Boolean> hasEntry(@AuthenticationPrincipal final Jwt token, @PathVariable final UUID id) {
        UserId userId = UserId.get(token);
        boolean hasEntry = cartService.hasEntry(userId, GameId.from(id));
        return ResponseEntity.ok(hasEntry);
    }

    @DeleteMapping("/entries/{id}")
    public ResponseEntity<Void> removeEntry(@AuthenticationPrincipal final Jwt token, @PathVariable final UUID id) {
        UserId userId = UserId.get(token);
        cartService.removeEntry(userId, GameId.from(id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@AuthenticationPrincipal final Jwt token) {
        UserId userId = UserId.get(token);
        cartService.clear(userId);
        return ResponseEntity.noContent().build();
    }
}
package be.kdg.team22.userservice.api.library;


import be.kdg.team22.userservice.api.library.models.LibraryGameModel;
import be.kdg.team22.userservice.api.library.models.LibraryGamesModel;
import be.kdg.team22.userservice.application.library.FilterQuery;
import be.kdg.team22.userservice.application.library.LibraryService;
import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/library")
public class LibraryController {
    private final LibraryService service;

    public LibraryController(final LibraryService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<List<LibraryGameModel>> getMyLibrary(
            @AuthenticationPrincipal final Jwt token,
            @RequestParam(required = false) final String query,
            @RequestParam(required = false) final Boolean favourite) {
        ProfileId userId = ProfileId.get(token);

        FilterQuery filter = new FilterQuery();
        filter.query = Optional.ofNullable(query);
        filter.favourite = Optional.ofNullable(favourite);

        LibraryGamesModel model = service.getLibraryForUser(userId, filter);
        return ResponseEntity.ok(model.games());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Boolean> ownsGame(@AuthenticationPrincipal final Jwt token, @PathVariable final UUID id) {
        ProfileId userId = ProfileId.get(token);
        GameId gameId = GameId.from(id);
        boolean ownsGame = service.ownsGame(userId, gameId);
        return ResponseEntity.ok(ownsGame);
    }

    @GetMapping("/{id}/favourite")
    public ResponseEntity<Boolean> getFavourite(@AuthenticationPrincipal final Jwt token, @PathVariable final UUID id) {
        ProfileId userId = ProfileId.get(token);
        GameId gameId = GameId.from(id);
        boolean favourite = service.isFavourite(userId, gameId);
        return ResponseEntity.ok(favourite);
    }

    @PatchMapping("/{id}/favourite")
    public ResponseEntity<Void> favouriteGame(@AuthenticationPrincipal final Jwt token, @PathVariable final UUID id) {
        ProfileId userId = ProfileId.get(token);
        GameId gameId = GameId.from(id);
        service.markFavourite(userId, gameId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/unfavourite")
    public ResponseEntity<Void> unfavouriteGame(@AuthenticationPrincipal final Jwt token, @PathVariable final UUID id) {
        ProfileId userId = ProfileId.get(token);
        GameId gameId = GameId.from(id);
        service.unmarkFavourite(userId, gameId);
        return ResponseEntity.noContent().build();
    }
}
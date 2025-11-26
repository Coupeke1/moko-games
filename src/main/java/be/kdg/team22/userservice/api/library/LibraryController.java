package be.kdg.team22.userservice.api.library;


import be.kdg.team22.userservice.api.library.models.LibraryGamesModel;
import be.kdg.team22.userservice.application.library.LibraryService;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/library")
public class LibraryController {
    private final LibraryService service;

    public LibraryController(final LibraryService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<LibraryGamesModel> getMyLibrary(
            @AuthenticationPrincipal final Jwt token
    ) {
        ProfileId userId = ProfileId.get(token);

        LibraryGamesModel model = service.getLibraryForUser(userId, token);

        return ResponseEntity.ok(model);
    }
}
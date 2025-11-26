package be.kdg.team22.userservice.api.library;


import be.kdg.team22.userservice.api.library.models.LibraryGamesModel;
import be.kdg.team22.userservice.application.library.LibraryService;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            @AuthenticationPrincipal final Jwt token,
            @RequestParam(required = false) final String filter,
            @RequestParam(required = false, defaultValue = "title_asc") final String order,
            @RequestParam(required = false, defaultValue = "100") final Integer limit
    ) {
        ProfileId userId = ProfileId.get(token);
        //TODO favourite filter
        LibraryGamesModel model = service.getLibraryForUser(
                userId, token, filter, order, limit
        );

        return ResponseEntity.ok(model);
    }
}
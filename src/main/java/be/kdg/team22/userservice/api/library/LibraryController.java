package be.kdg.team22.userservice.api.library;


import be.kdg.team22.userservice.api.library.models.LibraryGamesModel;
import be.kdg.team22.userservice.application.library.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/me")
    public ResponseEntity<LibraryGamesModel> getMyLibrary(
            @AuthenticationPrincipal Jwt token
    ) {
        UUID userId = UUID.fromString(token.getSubject());

        LibraryGamesModel model = libraryService.getLibraryForUser(userId, token);

        return ResponseEntity.ok(model);
    }
}
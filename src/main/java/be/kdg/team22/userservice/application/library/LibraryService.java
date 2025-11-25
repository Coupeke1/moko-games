package be.kdg.team22.userservice.application.library;

import be.kdg.team22.userservice.api.library.models.LibraryGameModel;
import be.kdg.team22.userservice.api.library.models.LibraryGamesModel;
import be.kdg.team22.userservice.domain.library.LibraryEntry;
import be.kdg.team22.userservice.domain.library.LibraryRepository;
import be.kdg.team22.userservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.userservice.infrastructure.games.GameDetailsResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final ExternalGamesRepository gamesRepository;

    public LibraryService(
            LibraryRepository libraryRepository,
            ExternalGamesRepository gamesRepository
    ) {
        this.libraryRepository = libraryRepository;
        this.gamesRepository = gamesRepository;
    }

    public LibraryGamesModel getLibraryForUser(UUID userId, Jwt token) {

        List<LibraryEntry> entries = libraryRepository.findByUserId(userId);

        List<LibraryGameModel> models = entries.stream()
                .map(entry -> {
                    GameDetailsResponse game = gamesRepository.getGame(entry.gameId(), token);

                    return new LibraryGameModel(
                            game.id(),
                            game.title(),
                            game.description(),
                            game.price(),
                            game.imageUrl(),
                            game.storeUrl(),
                            entry.purchasedAt()
                    );
                })
                .toList();

        return new LibraryGamesModel(models);
    }
}
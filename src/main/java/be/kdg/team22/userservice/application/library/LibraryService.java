package be.kdg.team22.userservice.application.library;

import be.kdg.team22.userservice.api.library.models.LibraryGameModel;
import be.kdg.team22.userservice.api.library.models.LibraryGamesModel;
import be.kdg.team22.userservice.domain.library.LibraryEntry;
import be.kdg.team22.userservice.domain.library.LibraryRepository;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.userservice.infrastructure.games.GameDetailsResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public LibraryGamesModel getLibraryForUser(ProfileId userId, Jwt token) {

        List<LibraryEntry> entries = libraryRepository.findByUserId(userId.value());

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
package be.kdg.team22.userservice.application.library;

import be.kdg.team22.userservice.api.library.models.LibraryGameModel;
import be.kdg.team22.userservice.api.library.models.LibraryGamesModel;
import be.kdg.team22.userservice.domain.library.LibraryRepository;
import be.kdg.team22.userservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.userservice.infrastructure.games.GameDetailsResponse;
import be.kdg.team22.userservice.infrastructure.library.jpa.LibraryEntryEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final ExternalGamesRepository gamesRepository;

    public LibraryService(
            LibraryRepository libraryRepo,
            ExternalGamesRepository gameClient
    ) {
        this.libraryRepository = libraryRepo;
        this.gamesRepository = gameClient;
    }

    public LibraryGamesModel getLibraryForUser(UUID userId) {

        List<LibraryEntryEntity> entries = libraryRepository.findByUserId(userId);

        List<LibraryGameModel> models = entries.stream()
                .map(entry -> {
                    GameDetailsResponse game = gamesRepository.getGame(entry.gameId());

                    String url = game.storeUrl() != null ? game.storeUrl() : null;

                    return new LibraryGameModel(
                            game.id(),
                            game.title(),
                            game.description(),
                            game.price(),
                            game.imageUrl(),
                            url,
                            entry.purchasedAt()
                    );
                })
                .toList();

        return new LibraryGamesModel(models);
    }
}

package be.kdg.team22.userservice.application.library;

import be.kdg.team22.userservice.api.library.models.LibraryGameModel;
import be.kdg.team22.userservice.api.library.models.LibraryGamesModel;
import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.library.LibraryEntry;
import be.kdg.team22.userservice.domain.library.LibraryRepository;
import be.kdg.team22.userservice.domain.library.exceptions.LibraryException;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.userservice.infrastructure.games.GameDetailsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class LibraryService {
    private static final Logger logger = LoggerFactory.getLogger(LibraryService.class);

    private final LibraryRepository libraryRepository;
    private final ExternalGamesRepository gamesRepository;

    public LibraryService(final LibraryRepository libraryRepository, final ExternalGamesRepository gamesRepository) {
        this.libraryRepository = libraryRepository;
        this.gamesRepository = gamesRepository;
    }

    public LibraryGamesModel getLibraryForUser(final ProfileId userId, final Jwt token, final FilterQuery filter) {
        List<LibraryEntry> entries = libraryRepository.findByUserId(userId.value());

        List<LibraryGameModel> games = entries.stream().map(entry -> {
            GameDetailsResponse game = gamesRepository.getGame(entry.gameId().value(), token);

            return new LibraryGameModel(game.id(), game.title(), game.description(), game.price(), game.image(), entry.purchasedAt(), entry.favourite());
        }).sorted(Comparator.comparing(LibraryGameModel::title)).toList();

        if (filter.query.isPresent() && !filter.query.get().isBlank()) {
            String query = filter.query.get();

            games = games.stream().filter(game -> (game.title() != null && game.title().toLowerCase().contains(query.toLowerCase())) || (game.description() != null && game.description().toLowerCase().contains(query.toLowerCase()))).toList();
        }

        return new LibraryGamesModel(games);
    }

    public boolean isFavourite(final ProfileId userId, final GameId gameId) {
        LibraryEntry entry = libraryRepository.findByUserIdAndGameId(userId.value(), gameId.value()).orElseThrow(LibraryException::notInLibrary);
        return entry.favourite();
    }

    public void markFavourite(final ProfileId userId, final GameId gameId) {
        LibraryEntry entry = libraryRepository.findByUserIdAndGameId(userId.value(), gameId.value()).orElseThrow(LibraryException::notInLibrary);

        LibraryEntry updated = entry.markFavourite();
        libraryRepository.save(updated);
    }

    public void unmarkFavourite(final ProfileId userId, final GameId gameId) {
        LibraryEntry entry = libraryRepository.findByUserIdAndGameId(userId.value(), gameId.value()).orElseThrow(LibraryException::notInLibrary);

        LibraryEntry updated = entry.unmarkFavourite();
        libraryRepository.save(updated);
    }

    public void addGameToLibrary(final ProfileId userId, final GameId gameId) {
        if (libraryRepository.findByUserIdAndGameId(userId.value(), gameId.value()).isPresent()) {
            logger.debug("Game {} already in library for user {}, skipping", gameId.value(), userId.value());
            return;
        }

        LibraryEntry entry = new LibraryEntry(
                null,
                userId,
                gameId,
                Instant.now(),
                false
        );
        libraryRepository.save(entry);
        logger.debug("Added game {} to library for user {}", gameId.value(), userId.value());
    }
}
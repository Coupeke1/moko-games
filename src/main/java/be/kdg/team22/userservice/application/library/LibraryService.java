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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final ExternalGamesRepository gamesRepository;

    public LibraryService(final LibraryRepository libraryRepository, final ExternalGamesRepository gamesRepository) {
        this.libraryRepository = libraryRepository;
        this.gamesRepository = gamesRepository;
    }

    public LibraryGamesModel getLibraryForUser(final ProfileId userId, final Jwt token, final String filter, final Boolean favourite, final String order, final Integer limit) {
        List<LibraryEntry> entries = libraryRepository.findByUserId(userId.value());

        List<LibraryGameModel> games = entries.stream().map(entry -> {
            GameDetailsResponse game = gamesRepository.getGame(entry.gameId().value(), token);

            return new LibraryGameModel(game.id(), game.title(), game.description(), game.price(), game.image(), entry.purchasedAt(), entry.favourite());
        }).toList();

        if (filter != null && !filter.isBlank()) {
            final String lower = filter.toLowerCase();
            games = games.stream().filter(g -> (g.title() != null && g.title().toLowerCase().contains(lower)) || (g.description() != null && g.description().toLowerCase().contains(lower))).toList();
        }

        if (Boolean.TRUE.equals(favourite)) {
            games = games.stream().filter(LibraryGameModel::favourite).toList();
        }

        Comparator<LibraryGameModel> comparator = switch (order) {
            case "title_desc" ->
                    Comparator.comparing(LibraryGameModel::title, Comparator.nullsLast(String::compareToIgnoreCase)).reversed();
            case "purchased_asc" ->
                    Comparator.comparing(LibraryGameModel::purchasedAt);
            case "purchased_desc" ->
                    Comparator.comparing(LibraryGameModel::purchasedAt).reversed();
            default ->
                    Comparator.comparing(LibraryGameModel::title, Comparator.nullsLast(String::compareToIgnoreCase));
        };

        games = games.stream().sorted(comparator).toList();

        if (limit != null && limit > 0) {
            games = games.stream().limit(limit).toList();
        }

        return new LibraryGamesModel(games);
    }

    public boolean isFavourite(ProfileId userId, GameId gameId) {
        LibraryEntry entry = libraryRepository.findByUserIdAndGameId(userId.value(), gameId.value()).orElseThrow(LibraryException::notInLibrary);
        return entry.favourite();
    }

    public void markFavourite(ProfileId userId, GameId gameId) {
        LibraryEntry entry = libraryRepository.findByUserIdAndGameId(userId.value(), gameId.value()).orElseThrow(LibraryException::notInLibrary);

        LibraryEntry updated = entry.markFavourite();
        libraryRepository.save(updated);
    }

    public void unmarkFavourite(ProfileId userId, GameId gameId) {
        LibraryEntry entry = libraryRepository.findByUserIdAndGameId(userId.value(), gameId.value()).orElseThrow(LibraryException::notInLibrary);

        LibraryEntry updated = entry.unmarkFavourite();
        libraryRepository.save(updated);
    }
}
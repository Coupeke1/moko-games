package be.kdg.team22.userservice.application.profile;

import be.kdg.team22.userservice.api.achievement.models.AchievementModel;
import be.kdg.team22.userservice.api.profile.models.FavouriteGameModel;
import be.kdg.team22.userservice.api.profile.models.FilteredProfileModel;
import be.kdg.team22.userservice.domain.achievement.AchievementRepository;
import be.kdg.team22.userservice.domain.library.LibraryEntry;
import be.kdg.team22.userservice.domain.library.LibraryRepository;
import be.kdg.team22.userservice.domain.library.exceptions.ExternalGameNotFoundException;
import be.kdg.team22.userservice.domain.profile.*;
import be.kdg.team22.userservice.domain.profile.exceptions.CannotUpdateProfileException;
import be.kdg.team22.userservice.domain.profile.exceptions.ClaimNotFoundException;
import be.kdg.team22.userservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.userservice.infrastructure.games.GameDetailsResponse;
import be.kdg.team22.userservice.infrastructure.image.ExternalImageRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ExternalImageRepository imageRepository;
    private final AchievementRepository achievementRepository;
    private final LibraryRepository libraryRepository;
    private final ExternalGamesRepository gamesRepository;

    public ProfileService(final ProfileRepository profileRepository,
                          final ExternalImageRepository imageRepository,
                          final AchievementRepository achievementRepository,
                          final LibraryRepository libraryRepository,
                          final ExternalGamesRepository gamesRepository) {
        this.profileRepository = profileRepository;
        this.imageRepository = imageRepository;
        this.achievementRepository = achievementRepository;
        this.libraryRepository = libraryRepository;
        this.gamesRepository = gamesRepository;
    }

    public Profile getOrCreate(final Jwt token) {
        ProfileId id = ProfileId.get(token);
        ProfileName username = getUsername(token);
        ProfileEmail email = getEmail(token);
        String description = "Hey there! I am using Moko.";
        String image = imageRepository.get().url();

        return profileRepository.findById(id).orElseGet(() -> {
            Profile profile = new Profile(id, username, email, description, image);
            profileRepository.save(profile);
            return profile;
        });
    }

    public Profile getById(final ProfileId id) {
        return profileRepository.findById(id).orElseThrow(id::notFound);
    }

    public FilteredProfileModel getByIdAndPreferences(final ProfileId profileId, final Jwt token) {
        Profile profile = getById(profileId);
        Modules modules = profile.modules();
        Statistics stats = profile.statistics();

        List<AchievementModel> achievements = null;
        if (modules.achievements()) {
            achievements = achievementRepository.findByProfile(profileId)
                    .stream()
                    .map(a -> {
                        GameDetailsResponse game = null;

                        try {
                            game = gamesRepository.getGame(a.gameId(), token);
                        } catch (ExternalGameNotFoundException e) {
                            // ignore, game stays null
                        }

                        return AchievementModel.from(a, game);
                    })
                    .toList();
        }

        List<FavouriteGameModel> favouriteGames = null;
        if (modules.favourites()) {
            List<LibraryEntry> entries = libraryRepository.findByUserId(profileId.value());

            favouriteGames = entries.stream()
                    .filter(LibraryEntry::favourite)
                    .map(entry -> {
                        GameDetailsResponse game = gamesRepository.getGame(entry.gameId().value(), token);
                        return new FavouriteGameModel(
                                game.id(),
                                game.title(),
                                game.description(),
                                game.image()
                        );
                    })
                    .toList();
        }

        return new FilteredProfileModel(
                profile.id().value(),
                profile.username().value(),
                profile.description(),
                profile.image(),
                stats.level(),
                stats.playTime(),
                achievements,
                favouriteGames
        );
    }

    public Profile getByUsername(final ProfileName username) {
        return profileRepository.findByUsername(username).orElseThrow(username::notFound);
    }

    public String changeDescription(final Profile profile, final String description) {
        if (description.equals(profile.description()))
            throw CannotUpdateProfileException.description(profile.id());

        profile.updateDescription(description);
        profileRepository.save(profile);

        return profile.description();
    }

    public String changeImage(final Profile profile, final String image) {
        if (image.equals(profile.image()))
            throw CannotUpdateProfileException.image(profile.id());

        profile.updateImage(image);
        profileRepository.save(profile);

        return profile.image();
    }

    public Modules changeModules(final Profile profile, final Modules modules) {
        if (modules.equals(profile.modules()))
            throw CannotUpdateProfileException.modules(profile.id());

        profile.updateModules(modules);
        profileRepository.save(profile);

        return profile.modules();
    }

    private ProfileName getUsername(final Jwt token) {
        if (token.hasClaim("preferred_username"))
            return new ProfileName(token.getClaimAsString("preferred_username"));

        if (token.hasClaim("username"))
            return new ProfileName(token.getClaimAsString("username"));

        throw ClaimNotFoundException.username();
    }

    private ProfileEmail getEmail(final Jwt token) {
        if (token.hasClaim("email"))
            return new ProfileEmail(token.getClaimAsString("email"));

        throw ClaimNotFoundException.email();
    }

    public NotificationPreferences changePreferences(final Profile profile,
                                                     final NotificationPreferences prefs) {
        if (prefs.equals(profile.preferences()))
            throw CannotUpdateProfileException.preferences(profile.id());

        profile.updatePreferences(prefs);
        profileRepository.save(profile);

        return profile.preferences();
    }

    public Profile createBotProfile() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        ProfileName username = BotNameGenerator.randomName();
        ProfileEmail email = BotNameGenerator.randomEmail(username);
        String description = "Hi, I'm a Moko bot.";
        String image = imageRepository.get().url();

        Profile profile = new Profile(id, username, email, description, image);
        profileRepository.save(profile);

        return profile;
    }

    public void addLevels(final ProfileId id, final int amount) {
        Profile profile = getById(id);
        profile.addLevels(amount);
        profileRepository.save(profile);
    }
}
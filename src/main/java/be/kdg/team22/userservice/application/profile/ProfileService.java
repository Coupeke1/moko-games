package be.kdg.team22.userservice.application.profile;

import be.kdg.team22.userservice.api.achievement.models.AchievementModel;
import be.kdg.team22.userservice.api.profile.models.FavouriteGameModel;
import be.kdg.team22.userservice.api.profile.models.FilteredProfileModel;
import be.kdg.team22.userservice.api.profile.models.StatisticsModel;
import be.kdg.team22.userservice.application.achievement.AchievementService;
import be.kdg.team22.userservice.domain.library.LibraryEntry;
import be.kdg.team22.userservice.domain.library.LibraryRepository;
import be.kdg.team22.userservice.domain.profile.*;
import be.kdg.team22.userservice.domain.profile.exceptions.CannotUpdateProfileException;
import be.kdg.team22.userservice.domain.profile.exceptions.ClaimNotFoundException;
import be.kdg.team22.userservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.userservice.infrastructure.games.GameDetailsResponse;
import be.kdg.team22.userservice.infrastructure.image.ExternalImageRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ExternalImageRepository imageRepository;
    private final ObjectProvider<AchievementService> achievementServiceProvider;
    private final LibraryRepository libraryRepository;
    private final ExternalGamesRepository gamesRepository;

    public ProfileService(final ProfileRepository profileRepository, final ExternalImageRepository imageRepository, final LibraryRepository libraryRepository, final ExternalGamesRepository gamesRepository, final ObjectProvider<AchievementService> achievementServiceProvider) {
        this.profileRepository = profileRepository;
        this.imageRepository = imageRepository;
        this.libraryRepository = libraryRepository;
        this.gamesRepository = gamesRepository;
        this.achievementServiceProvider = achievementServiceProvider;
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

    public FilteredProfileModel getByIdAndPreferences(final ProfileId id) {
        Profile profile = getById(id);
        return getFilteredProfile(profile);
    }

    public FilteredProfileModel getByUsernameAndPreferences(final ProfileName username) {
        Profile profile = getByUsername(username);
        return getFilteredProfile(profile);
    }

    private FilteredProfileModel getFilteredProfile(final Profile profile) {
        List<AchievementModel> achievements = profile.modules().achievements() ? getAchievements(profile.id()) : null;
        List<FavouriteGameModel> favourites = profile.modules().favourites() ? getFavourites(profile.id()) : null;

        return new FilteredProfileModel(profile.id().value(), profile.username().value(), profile.email().value(), profile.description(), profile.image(), new StatisticsModel(profile.statistics().level(), profile.statistics().playTime()), achievements, favourites);
    }

    private List<AchievementModel> getAchievements(final ProfileId id) {
        return achievementServiceProvider.getObject().findModelsByProfile(id);
    }

    private List<FavouriteGameModel> getFavourites(final ProfileId id) {
        List<LibraryEntry> entries = libraryRepository.findByUserId(id.value());

        return entries.stream().filter(LibraryEntry::favourite).map(entry -> {
            GameDetailsResponse game = gamesRepository.getGame(entry.gameId().value());
            return new FavouriteGameModel(game.id(), game.title(), game.description(), game.image());
        }).toList();
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

    public Notifications changeNotifications(final Profile profile, final Notifications notifications) {
        if (notifications.equals(profile.notifications()))
            throw CannotUpdateProfileException.notifications(profile.id());

        profile.updateNotifications(notifications);
        profileRepository.save(profile);

        return profile.notifications();
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

    public Profile createBotProfile(final String description) {
        Profile profile = createBotProfile(BotId.create(), description);
        profileRepository.save(profile);
        return profile;
    }

    public Profile getOrCreateBotProfile(final BotId botId, final String description) {
        return profileRepository.findById(ProfileId.from(botId)).orElseGet(() -> {
            Profile profile = createBotProfile(botId, description);
            profileRepository.save(profile);
            return profile;
        });
    }

    private Profile createBotProfile(final BotId botId, final String description) {
        ProfileId id = ProfileId.from(botId);
        ProfileName username = Generator.name();
        ProfileEmail email = Generator.email(username);
        String image = imageRepository.get().url();

        return new Profile(id, username, email, description != null ? description : "Hi, I'm a Moko bot.", image);
    }

    public void addLevels(final ProfileId id, final int amount) {
        Profile profile = getById(id);
        profile.addLevels(amount);
        profileRepository.save(profile);
    }
}
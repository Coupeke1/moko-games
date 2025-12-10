package be.kdg.team22.userservice.application.profile;

import be.kdg.team22.userservice.domain.profile.*;
import be.kdg.team22.userservice.domain.profile.exceptions.CannotUpdateProfileException;
import be.kdg.team22.userservice.domain.profile.exceptions.ClaimNotFoundException;
import be.kdg.team22.userservice.infrastructure.image.ExternalImageRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ExternalImageRepository imageRepository;

    public ProfileService(final ProfileRepository profileRepository, ExternalImageRepository imageRepository) {
        this.profileRepository = profileRepository;
        this.imageRepository = imageRepository;
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
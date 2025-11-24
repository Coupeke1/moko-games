package be.kdg.team22.userservice.application.profile;

import be.kdg.team22.userservice.domain.profile.*;
import be.kdg.team22.userservice.domain.profile.exceptions.CannotUpdateProfileException;
import be.kdg.team22.userservice.domain.profile.exceptions.ClaimNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileService {
    private final ProfileRepository repository;

    public ProfileService(final ProfileRepository repository) {
        this.repository = repository;
    }

    public Profile getOrCreate(final Jwt token) {
        ProfileId id = ProfileId.get(token);
        ProfileName username = getUsername(token);
        ProfileEmail email = getEmail(token);
        String description = "Hey there! I am using Moko.";

        return repository.findById(id).orElseGet(() -> {
            Profile profile = new Profile(id, username, email, description);
            repository.save(profile);
            return profile;
        });
    }

    public Profile getById(final ProfileId id) {
        return repository.findById(id).orElseThrow(id::notFound);
    }

    public Profile getByUsername(final ProfileName username) {
        return repository.findByUsername(username).orElseThrow(username::notFound);
    }

    public String changeDescription(final Profile profile, final String description) {
        if (description.equals(profile.description()))
            throw CannotUpdateProfileException.description(profile.id());

        profile.updateDescription(description);
        repository.save(profile);

        return profile.description();
    }

    public Modules changeModules(final Profile profile, final Modules modules) {
        if (modules.equals(profile.modules()))
            throw CannotUpdateProfileException.modules(profile.id());

        profile.updateModules(modules);
        repository.save(profile);

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
}
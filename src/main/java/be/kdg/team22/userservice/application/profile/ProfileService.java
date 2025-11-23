package be.kdg.team22.userservice.application.profile;

import be.kdg.team22.userservice.domain.profile.Profile;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.domain.profile.ProfileName;
import be.kdg.team22.userservice.domain.profile.ProfileRepository;
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

        return repository.findById(id).orElseGet(() -> {
            Profile profile = new Profile(id, username);
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

    private ProfileName getUsername(final Jwt token) {
        if (token.hasClaim("preferred_username"))
            return new ProfileName(token.getClaimAsString("preferred_username"));

        if (token.hasClaim("username"))
            return new ProfileName(token.getClaimAsString("username"));

        throw ClaimNotFoundException.username();
    }
}
package be.kdg.team22.userservice.application.profile;

import be.kdg.team22.userservice.domain.profile.Profile;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.domain.profile.ProfileRepository;
import be.kdg.team22.userservice.domain.profile.exceptions.ClaimNotFoundException;
import be.kdg.team22.userservice.domain.profile.exceptions.NotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileService {
    private final ProfileRepository repository;

    public ProfileService(ProfileRepository repository) {
        this.repository = repository;
    }

    public Profile getOrCreate(Jwt token) {
        ProfileId id = ProfileId.get(token);

        String username = getUsername(token);
        String email = getEmail(token);

        return repository.findById(id).orElseGet(() -> {
            Profile profile = new Profile(id, username, email);
            repository.save(profile);
            return profile;
        });
    }

    public Profile update(Jwt token, String username, String email) {
        ProfileId id = ProfileId.get(token);
        Profile profile = repository.findById(id).orElseThrow(id::notFound);

        profile.update(username, email);
        repository.save(profile);

        return profile;
    }

    public Profile getById(ProfileId id) {
        return repository.findById(id).orElseThrow(id::notFound);
    }

    public Profile getByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new NotFoundException(username));
    }

    private String getUsername(Jwt token) {
        if (token.hasClaim("preferred_username"))
            return token.getClaimAsString("preferred_username");

        if (token.hasClaim("username"))
            return token.getClaimAsString("username");

        throw ClaimNotFoundException.username();
    }

    private String getEmail(Jwt token) {
        if (!token.hasClaim("email"))
            throw ClaimNotFoundException.email();

        return token.getClaimAsString("email");
    }
}
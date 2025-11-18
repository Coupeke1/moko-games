package be.kdg.team22.userservice.profile.application.profile;

import be.kdg.team22.userservice.profile.domain.profile.Profile;
import be.kdg.team22.userservice.profile.domain.profile.ProfileId;
import be.kdg.team22.userservice.profile.domain.profile.ProfileRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class ProfileService {
    private final ProfileRepository profiles;

    public ProfileService(ProfileRepository profiles) {
        this.profiles = profiles;
    }

    public Profile getOrCreate(Jwt jwt) {
        String subject = jwt.getSubject();
        if (subject == null) {
            throw new MissingRequiredClaimException("Missing 'sub' claim in JWT");
        }

        UUID uuid = UUID.fromString(subject);
        ProfileId id = new ProfileId(uuid);

        return profiles.findById(id)
                .orElseGet(() -> {
                    String username = extractUsername(jwt);
                    String email = jwt.getClaimAsString("email");
                    if (email == null) {
                        throw new MissingRequiredClaimException("Missing 'email' claim in JWT");
                    }
                    Profile profile = Profile.createNew(id, username, email);
                    profiles.save(profile);
                    return profile;
                });
    }

    public Profile getById(ProfileId id) {
        return profiles.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Profile not found: " + id.value()));
    }

    private String extractUsername(Jwt jwt) {
        String preferred = jwt.getClaimAsString("preferred_username");
        if (preferred != null) return preferred;

        String username = jwt.getClaimAsString("username");
        if (username != null) return username;

        String subject = jwt.getSubject();
        if (subject == null) {
            throw new MissingRequiredClaimException("Missing both username and subject claims in JWT");
        }

        return subject;
    }
}

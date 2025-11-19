package be.kdg.team22.userservice.profile.application.profile;

import be.kdg.team22.userservice.profile.domain.profile.Profile;
import be.kdg.team22.userservice.profile.domain.profile.ProfileId;
import be.kdg.team22.userservice.profile.domain.profile.ProfileRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
public class ProfileService {
    private final ProfileRepository profiles;

    public ProfileService(ProfileRepository profiles) {
        this.profiles = profiles;
    }

    public Profile getOrCreate(Jwt jwt) {
        ProfileId id = ProfileId.get(jwt);

        String username = extractUsername(jwt);
        String email = extractRequired(jwt, "email");

        return profiles.findById(id)
                .orElseGet(() -> {
                    Profile profile = Profile.createNew(id, username, email);
                    profiles.save(profile);
                    return profile;
                });
    }

    public Profile update(Jwt jwt, String newUsername, String newEmail) {
        ProfileId id = ProfileId.get(jwt);

        Profile profile = profiles.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Profile not found"));

        profile.update(newUsername, newEmail);
        profiles.save(profile);

        return profile;
    }

    public Profile getById(ProfileId id) {
        return profiles.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Profile not found"));
    }

    private String extractRequired(Jwt jwt, String claim) {
        String jwtClaim = jwt.getClaimAsString(claim);
        if (jwtClaim == null)
            throw new MissingRequiredClaimException("Missing claim: " + claim);
        return jwtClaim;
    }

    private String extractUsername(Jwt jwt) {
        if (jwt.hasClaim("preferred_username"))
            return jwt.getClaimAsString("preferred_username");

        if (jwt.hasClaim("username"))
            return jwt.getClaimAsString("username");

        return extractRequired(jwt, "sub");
    }
}

package be.kdg.team22.userservice.profile.api.profile;

import be.kdg.team22.userservice.profile.application.profile.ProfileService;
import be.kdg.team22.userservice.profile.domain.profile.Profile;
import be.kdg.team22.userservice.profile.domain.profile.ProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileModel> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        Profile profile = service.getOrCreate(jwt);
        return ResponseEntity.ok(ProfileModel.from(profile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileModel> getById(@PathVariable UUID id) {
        Profile profile = service.getById(new ProfileId(id));
        return ResponseEntity.ok(ProfileModel.from(profile));
    }
}

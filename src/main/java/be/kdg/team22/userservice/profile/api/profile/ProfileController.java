package be.kdg.team22.userservice.profile.api.profile;

import be.kdg.team22.userservice.profile.api.profile.models.EditProfileModel;
import be.kdg.team22.userservice.profile.api.profile.models.ProfileModel;
import be.kdg.team22.userservice.profile.application.profile.ProfileService;
import be.kdg.team22.userservice.profile.domain.profile.Profile;
import be.kdg.team22.userservice.profile.domain.profile.ProfileId;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@Validated
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

    @PatchMapping("/me")
    public ResponseEntity<ProfileModel> updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody EditProfileModel body
    ) {
        Profile profile = service.update(jwt, body.username(), body.email());
        return ResponseEntity.ok(ProfileModel.from(profile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileModel> getById(@PathVariable UUID id) {
        Profile profile = service.getById(new ProfileId(id));
        return ResponseEntity.ok(ProfileModel.from(profile));
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<ProfileModel> getByUsername(@PathVariable String username) {
        Profile profile = service.getByUsername(username);
        return ResponseEntity.ok(ProfileModel.from(profile));
    }
}

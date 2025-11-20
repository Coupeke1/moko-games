package be.kdg.team22.userservice.api.profile;

import be.kdg.team22.userservice.api.profile.models.EditProfileModel;
import be.kdg.team22.userservice.api.profile.models.ProfileModel;
import be.kdg.team22.userservice.application.profile.ProfileService;
import be.kdg.team22.userservice.domain.profile.Profile;
import be.kdg.team22.userservice.domain.profile.ProfileId;
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
    public ResponseEntity<ProfileModel> getMyProfile(@AuthenticationPrincipal final Jwt jwt) {
        Profile profile = service.getOrCreate(jwt);
        return ResponseEntity.ok(ProfileModel.from(profile));
    }

    @PatchMapping("/me")
    public ResponseEntity<ProfileModel> updateMyProfile(@AuthenticationPrincipal final Jwt jwt, @Valid @RequestBody final EditProfileModel body) {
        Profile profile = service.update(jwt, body.username(), body.email());
        return ResponseEntity.ok(ProfileModel.from(profile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileModel> getById(@PathVariable final UUID id) {
        Profile profile = service.getById(new ProfileId(id));
        return ResponseEntity.ok(ProfileModel.from(profile));
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<ProfileModel> getByUsername(@PathVariable final String username) {
        Profile profile = service.getByUsername(username);
        return ResponseEntity.ok(ProfileModel.from(profile));
    }
}
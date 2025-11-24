package be.kdg.team22.userservice.api.profile;

import be.kdg.team22.userservice.api.profile.models.ModulesModel;
import be.kdg.team22.userservice.api.profile.models.ProfileModel;
import be.kdg.team22.userservice.application.profile.ProfileService;
import be.kdg.team22.userservice.domain.profile.Modules;
import be.kdg.team22.userservice.domain.profile.Profile;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.domain.profile.ProfileName;
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

    public ProfileController(final ProfileService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileModel> getMyProfile(@AuthenticationPrincipal final Jwt token) {
        Profile profile = service.getOrCreate(token);
        return ResponseEntity.ok(ProfileModel.from(profile));
    }

    @PatchMapping("/me/description")
    public ResponseEntity<String> updateMyDescription(@AuthenticationPrincipal final Jwt token, @RequestBody final String model) {
        Profile profile = service.getOrCreate(token);
        String description = service.changeDescription(profile, model);
        return ResponseEntity.ok(description);
    }

    @PatchMapping("/me/modules")
    public ResponseEntity<ModulesModel> updateMyModules(@AuthenticationPrincipal final Jwt token, @RequestBody final ModulesModel model) {
        Profile profile = service.getOrCreate(token);
        Modules modules = service.changeModules(profile, model.to());
        return ResponseEntity.ok(ModulesModel.from(modules));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileModel> getById(@PathVariable final UUID id) {
        Profile profile = service.getById(new ProfileId(id));
        return ResponseEntity.ok(ProfileModel.from(profile));
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<ProfileModel> getByUsername(@PathVariable final String username) {
        Profile profile = service.getByUsername(new ProfileName(username));
        return ResponseEntity.ok(ProfileModel.from(profile));
    }
}
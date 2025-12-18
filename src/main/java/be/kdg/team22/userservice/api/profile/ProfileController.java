package be.kdg.team22.userservice.api.profile;

import be.kdg.team22.userservice.api.profile.models.EditModulesModel;
import be.kdg.team22.userservice.api.profile.models.FilteredProfileModel;
import be.kdg.team22.userservice.api.profile.models.NotificationsModel;
import be.kdg.team22.userservice.api.profile.models.ProfileModel;
import be.kdg.team22.userservice.application.profile.ProfileService;
import be.kdg.team22.userservice.domain.profile.*;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/bot")
    public ResponseEntity<ProfileModel> createBotProfile() {
        Profile bot = service.createBotProfile();
        return ResponseEntity.status(HttpStatus.CREATED).body(ProfileModel.from(bot));
    }

    @GetMapping("/me/preferences/notifications")
    public ResponseEntity<NotificationsModel> getMyNotifications(@AuthenticationPrincipal final Jwt token) {
        Profile profile = service.getOrCreate(token);
        return ResponseEntity.ok(NotificationsModel.from(profile.notifications()));
    }

    @PatchMapping("/me/preferences/description")
    public ResponseEntity<String> updateMyDescription(@AuthenticationPrincipal final Jwt token, @RequestBody final String model) {
        Profile profile = service.getOrCreate(token);
        String description = service.changeDescription(profile, model);
        return ResponseEntity.ok(description);
    }

    @PatchMapping("/me/preferences/image")
    public ResponseEntity<String> updateMyImage(@AuthenticationPrincipal final Jwt token, @RequestBody final String model) {
        Profile profile = service.getOrCreate(token);
        String image = service.changeImage(profile, model);
        return ResponseEntity.ok(image);
    }

    @PatchMapping("/me/preferences/modules")
    public ResponseEntity<EditModulesModel> updateMyModules(@AuthenticationPrincipal final Jwt token, @RequestBody final EditModulesModel model) {
        Profile profile = service.getOrCreate(token);
        Modules modules = service.changeModules(profile, model.to());
        return ResponseEntity.ok(EditModulesModel.from(modules));
    }

    @PatchMapping("/me/preferences/notifications")
    public ResponseEntity<NotificationsModel> updateMyNotifications(@AuthenticationPrincipal final Jwt token, @RequestBody final NotificationsModel model) {
        Profile profile = service.getOrCreate(token);
        Notifications notifications = service.changeNotifications(profile, model.to());
        return ResponseEntity.ok(NotificationsModel.from(notifications));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilteredProfileModel> getById(@PathVariable final UUID id, @AuthenticationPrincipal final Jwt token) {
        FilteredProfileModel profile = service.getByIdAndPreferences(new ProfileId(id));
        return ResponseEntity.ok(profile);
    }

    @GetMapping("{id}/preferences/notifications")
    public ResponseEntity<NotificationsModel> getPreferencesById(@PathVariable final UUID id) {
        Profile profile = service.getById(new ProfileId(id));
        return ResponseEntity.ok(NotificationsModel.from(profile.notifications()));
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<ProfileModel> getByUsername(@PathVariable final String username) {
        Profile profile = service.getByUsername(new ProfileName(username));
        return ResponseEntity.ok(ProfileModel.from(profile));
    }
}
package be.kdg.team22.userservice.api.achievement;

import be.kdg.team22.userservice.api.achievement.models.AchievementListModel;
import be.kdg.team22.userservice.api.achievement.models.AchievementModel;
import be.kdg.team22.userservice.application.achievement.AchievementService;
import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {
    private final AchievementService service;

    public AchievementController(AchievementService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<AchievementListModel> getMyAchievements(@AuthenticationPrincipal Jwt token) {
        ProfileId profileId = ProfileId.get(token);
        List<AchievementModel> achievements = service.findAchievementsByProfile(profileId);
        return ResponseEntity.ok(new AchievementListModel(achievements));
    }

    @GetMapping("/me/{id}")
    public ResponseEntity<AchievementListModel> getMyAchievementsForGame(@PathVariable final UUID id, @AuthenticationPrincipal Jwt token) {
        ProfileId profileId = ProfileId.get(token);
        GameId gameId = GameId.from(id);
        List<AchievementModel> achievements = service.findAchievementsByProfileAndGame(profileId, gameId);
        return ResponseEntity.ok(new AchievementListModel(achievements));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AchievementListModel> getAchievementsForUser(@PathVariable UUID userId) {
        ProfileId profileId = ProfileId.from(userId);

        List<AchievementModel> achievements = service.findAchievementsByProfile(profileId);
        return ResponseEntity.ok(new AchievementListModel(achievements));
    }
}
package be.kdg.team22.userservice.api.achievement;

import be.kdg.team22.userservice.application.achievement.AchievementService;
import be.kdg.team22.userservice.domain.achievement.Achievement;
import be.kdg.team22.userservice.domain.achievement.AchievementCode;
import be.kdg.team22.userservice.domain.achievement.AchievementId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AchievementController.class)
class AchievementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AchievementService service;

    private UsernamePasswordAuthenticationToken authWithUser(UUID id) {
        Jwt jwt = Jwt.withTokenValue("token-" + id)
                .header("alg", "none")
                .subject(id.toString())
                .claim("preferred_username", "mathias")
                .claim("email", "m@a")
                .build();

        return new UsernamePasswordAuthenticationToken(jwt, jwt.getTokenValue(), List.of());
    }

    private Jwt extractJwt(UsernamePasswordAuthenticationToken auth) {
        return (Jwt) auth.getPrincipal();
    }

    private Achievement sampleAchievement(UUID userId) {
        return new Achievement(
                AchievementId.create(),
                new ProfileId(userId),
                UUID.randomUUID(),
                new AchievementCode("TICTACTOE_WIN"),
                Instant.parse("2024-01-01T10:00:00Z")
        );
    }

    @Test
    @DisplayName("GET /api/achievements/me → returns achievements for authenticated user")
    void getMyAchievements_success() throws Exception {
        UUID userId = UUID.randomUUID();
        ProfileId profileId = ProfileId.from(userId);
        UsernamePasswordAuthenticationToken auth = authWithUser(userId);

        Achievement achievement = sampleAchievement(userId);

        when(service.findByProfile(profileId))
                .thenReturn(List.of(achievement));

        mockMvc.perform(get("/api/achievements/me")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.achievements.length()").value(1))
                .andExpect(jsonPath("$.achievements[0].code").value("TICTACTOE_WIN"));

        Jwt jwt = extractJwt(auth);
        verify(service).findByProfile(profileId);
    }

    @Test
    @DisplayName("GET /api/achievements/me → empty list")
    void getMyAchievements_empty() throws Exception {
        UUID userId = UUID.randomUUID();
        ProfileId profileId = ProfileId.from(userId);
        UsernamePasswordAuthenticationToken auth = authWithUser(userId);

        when(service.findByProfile(profileId))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/achievements/me")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.achievements.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/achievements/me → 401 when no JWT")
    void getMyAchievements_noJwtUnauthorized() throws Exception {
        mockMvc.perform(get("/api/achievements/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/achievements/me → 400 when JWT subject is not a UUID")
    void getMyAchievements_invalidUuid() throws Exception {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("not-a-uuid")
                .claim("preferred_username", "mathias")
                .claim("email", "m@a")
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(jwt, jwt.getTokenValue(), List.of());

        mockMvc.perform(get("/api/achievements/me").with(authentication(auth)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/achievements/{userId} → returns achievements for any user")
    void getAchievementsForUser_success() throws Exception {
        UUID userId = UUID.randomUUID();
        ProfileId profileId = ProfileId.from(userId);

        Achievement achievement = sampleAchievement(userId);

        when(service.findByProfile(profileId)).thenReturn(List.of(achievement));

        UsernamePasswordAuthenticationToken auth = authWithUser(UUID.randomUUID());

        mockMvc.perform(get("/api/achievements/" + userId)
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.achievements.length()").value(1))
                .andExpect(jsonPath("$.achievements[0].code").value("TICTACTOE_WIN"));

        verify(service).findByProfile(profileId);
    }

    @Test
    @DisplayName("GET /api/achievements/{userId} → empty list")
    void getAchievementsForUser_empty() throws Exception {
        UUID userId = UUID.randomUUID();
        ProfileId profileId = ProfileId.from(userId);

        when(service.findByProfile(profileId)).thenReturn(List.of());

        UsernamePasswordAuthenticationToken auth = authWithUser(UUID.randomUUID());

        mockMvc.perform(get("/api/achievements/" + userId)
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.achievements.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/achievements/{userId} → 400 invalid UUID")
    void getAchievementsForUser_invalidUuid() throws Exception {

        UsernamePasswordAuthenticationToken auth = authWithUser(UUID.randomUUID());

        mockMvc.perform(get("/api/achievements/not-a-uuid")
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest());
    }
}
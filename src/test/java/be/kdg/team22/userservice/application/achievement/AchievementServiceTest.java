package be.kdg.team22.userservice.application.achievement;

import be.kdg.team22.userservice.application.profile.ProfileService;
import be.kdg.team22.userservice.domain.achievement.*;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.userservice.infrastructure.messaging.AchievementEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.anyInt;

class AchievementServiceTest {

    private final AchievementRepository repo = mock(AchievementRepository.class);
    private final AchievementEventPublisher achievementEventPublisher = mock(AchievementEventPublisher.class);
    private final ProfileService profileService = mock(ProfileService.class);
    private final ExternalGamesRepository externalGamesRepository = mock(ExternalGamesRepository.class);
    private final AchievementService service = new AchievementService(repo, achievementEventPublisher, profileService, externalGamesRepository);

    @Test
    @DisplayName("award(userId, code) → creëert nieuw achievements wanneer niet bestaat")
    void award_createsAchievement() {
        UUID userId = UUID.randomUUID();
        String code = "win_first_game";

        ProfileId profileId = new ProfileId(userId);
        AchievementCode achievementCode = new AchievementCode(code);

        when(repo.existsByProfileAndCode(profileId, achievementCode))
                .thenReturn(false);

        service.award(userId, code);

        verify(repo).save(argThat(a ->
                a.profileId().equals(profileId)
                        && a.code().equals(achievementCode)
                        && a.gameId() == null
                        && a.unlockedAt() != null
        ));
    }

    @Test
    @DisplayName("award(userId, gameId, code) → creëert achievements met gameId")
    void award_createsAchievementWithGameId() {
        UUID userId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        String code = "tictactoe.win";

        ProfileId profileId = new ProfileId(userId);
        AchievementCode achievementCode = new AchievementCode(code);

        when(repo.existsByProfileAndCode(profileId, achievementCode))
                .thenReturn(false);

        service.award(userId, gameId, code);

        verify(repo).save(argThat(a ->
                a.profileId().equals(profileId)
                        && a.gameId().equals(gameId)
                        && a.code().equals(achievementCode)
                        && a.unlockedAt() != null
        ));
    }

    @Test
    @DisplayName("award(...): bestaat achievements al → geen save()")
    void award_doesNotCreateDuplicate() {
        UUID userId = UUID.randomUUID();
        String code = "duplicate_code";

        ProfileId profileId = new ProfileId(userId);
        AchievementCode achievementCode = new AchievementCode(code);

        when(repo.existsByProfileAndCode(profileId, achievementCode))
                .thenReturn(true);

        service.award(userId, code);

        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("findByProfile → repo resultaat wordt teruggegeven")
    void findByProfile_returnsList() {
        ProfileId profileId = new ProfileId(UUID.randomUUID());

        Achievement a = new Achievement(
                AchievementId.create(),
                profileId,
                UUID.randomUUID(),
                new AchievementCode("won-first-game"),
                Instant.now()
        );

        when(repo.findByProfile(profileId)).thenReturn(List.of(a));

        List<Achievement> result = service.findByProfile(profileId);

        assertThat(result).containsExactly(a);
        verify(repo).findByProfile(profileId);
    }

    @Test
    @DisplayName("award(userId, code) → adds levels to profile based on achievements code")
    void award_addsLevelsToProfile() {
        UUID userId = UUID.randomUUID();
        String code = "TICTACTOE_WIN";

        ProfileId profileId = new ProfileId(userId);
        AchievementCode achievementCode = new AchievementCode(code);

        when(repo.existsByProfileAndCode(profileId, achievementCode))
                .thenReturn(false);

        service.award(userId, code);

        int expectedLevels = AchievementMetadata.getLevels(code);
        verify(profileService).addLevels(eq(profileId), eq(expectedLevels));
    }

    @Test
    @DisplayName("award(...): bestaat achievements al → geen levels toegevoegd")
    void award_doesNotAddLevelsWhenDuplicate() {
        UUID userId = UUID.randomUUID();
        String code = "TICTACTOE_WIN";

        ProfileId profileId = new ProfileId(userId);
        AchievementCode achievementCode = new AchievementCode(code);

        when(repo.existsByProfileAndCode(profileId, achievementCode))
                .thenReturn(true);

        service.award(userId, code);

        verify(profileService, never()).addLevels(any(), anyInt());
    }
}
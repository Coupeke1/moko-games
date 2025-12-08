package be.kdg.team22.userservice.application.achievement;

import be.kdg.team22.userservice.domain.achievement.Achievement;
import be.kdg.team22.userservice.domain.achievement.AchievementCode;
import be.kdg.team22.userservice.domain.achievement.AchievementId;
import be.kdg.team22.userservice.domain.achievement.AchievementRepository;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.infrastructure.messaging.AchievementEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class AchievementServiceTest {

    private final AchievementRepository repo = mock(AchievementRepository.class);
    private final AchievementEventPublisher achievementRepository = mock(AchievementEventPublisher.class);
    private final AchievementService service = new AchievementService(repo, achievementRepository);

    @Test
    @DisplayName("award(userId, code) → creëert nieuw achievement wanneer niet bestaat")
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
    @DisplayName("award(userId, gameId, code) → creëert achievement met gameId")
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
    @DisplayName("award(...): bestaat achievement al → geen save()")
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
}
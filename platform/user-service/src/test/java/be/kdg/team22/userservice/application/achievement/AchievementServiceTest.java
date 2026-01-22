package be.kdg.team22.userservice.application.achievement;

import be.kdg.team22.userservice.application.profile.ProfileService;
import be.kdg.team22.userservice.domain.achievement.Achievement;
import be.kdg.team22.userservice.domain.achievement.AchievementKey;
import be.kdg.team22.userservice.domain.achievement.AchievementRepository;
import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.infrastructure.games.AchievementDetailsResponse;
import be.kdg.team22.userservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.userservice.infrastructure.games.GameDetailsResponse;
import be.kdg.team22.userservice.infrastructure.messaging.AchievementEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AchievementServiceTest {

    private final AchievementRepository repo = mock(AchievementRepository.class);
    private final AchievementEventPublisher achievementEventPublisher = mock(AchievementEventPublisher.class);
    private final ProfileService profileService = mock(ProfileService.class);
    private final ExternalGamesRepository externalGamesRepository = mock(ExternalGamesRepository.class);
    private final AchievementService service = new AchievementService(repo, achievementEventPublisher, profileService, externalGamesRepository);

    @Test
    @DisplayName("award → creates achievement when not exists")
    void award_createsAchievement() {
        UUID userId = UUID.randomUUID();
        ProfileId profileId = new ProfileId(userId);
        String gameName = "TicTacToe";
        UUID gameUUID = UUID.randomUUID();
        GameId gameId = new GameId(gameUUID);
        AchievementKey key = new AchievementKey("TICTACTOE_WIN");

        GameDetailsResponse gameDetails = new GameDetailsResponse(gameUUID, "Engine", "TicTacToe", "A game", BigDecimal.TEN, "img.png", true);
        AchievementDetailsResponse achievementDetails = new AchievementDetailsResponse("TICTACTOE_WIN", "Win", "Win first game", 5);

        when(externalGamesRepository.getGame(gameName)).thenReturn(gameDetails);
        when(repo.existsByProfileGameAndKey(profileId, gameId, key)).thenReturn(false);
        when(externalGamesRepository.getAchievementDetails(gameId, key)).thenReturn(achievementDetails);

        service.award(profileId, gameName, key);

        verify(repo).save(argThat(a ->
                a.profileId().equals(profileId)
                        && a.gameId().value().equals(gameUUID)
                        && a.key().equals(key)
                        && a.unlockedAt() != null
        ));
        verify(profileService).addLevels(profileId, 5);
    }

    @Test
    @DisplayName("award → skips when achievement already exists")
    void award_doesNotCreateDuplicate() {
        UUID userId = UUID.randomUUID();
        ProfileId profileId = new ProfileId(userId);
        String gameName = "TicTacToe";
        UUID gameUUID = UUID.randomUUID();
        GameId gameId = new GameId(gameUUID);
        AchievementKey key = new AchievementKey("TICTACTOE_WIN");

        GameDetailsResponse gameDetails = new GameDetailsResponse(gameUUID, "Engine", "TicTacToe", "A game", BigDecimal.TEN, "img.png", true);

        when(externalGamesRepository.getGame(gameName)).thenReturn(gameDetails);
        when(repo.existsByProfileGameAndKey(profileId, gameId, key)).thenReturn(true);

        service.award(profileId, gameName, key);

        verify(repo, never()).save(any());
        verify(profileService, never()).addLevels(any(), anyInt());
    }

    @Test
    @DisplayName("findByProfile → returns list of achievements")
    void findByProfile_returnsList() {
        ProfileId profileId = new ProfileId(UUID.randomUUID());
        GameId gameId = new GameId(UUID.randomUUID());
        AchievementKey key = new AchievementKey("TEST_ACHIEVEMENT");

        Achievement a = Achievement.create(profileId, gameId, key);

        when(repo.findByProfile(profileId)).thenReturn(List.of(a));

        List<Achievement> result = service.findByProfile(profileId);

        assertThat(result).containsExactly(a);
        verify(repo).findByProfile(profileId);
    }
}
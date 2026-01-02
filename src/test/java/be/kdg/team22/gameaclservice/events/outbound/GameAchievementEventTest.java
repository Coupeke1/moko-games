package be.kdg.team22.gameaclservice.events.outbound;

import be.kdg.team22.gameaclservice.events.inbound.ChessAchievementEvent;
import be.kdg.team22.gameaclservice.events.inbound.ChessMessageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GameAchievementEvent")
class GameAchievementEventTest {

    @Test
    @DisplayName("should convert chess achievement event to game achievement event")
    void testConvertChessAchievementToGameAchievementEvent() {
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        ChessAchievementEvent chessEvent = new ChessAchievementEvent(
                gameId,
                playerId,
                "John",
                "first_move",
                "Make your first move",
                ChessMessageType.ACHIEVEMENT_ACQUIRED,
                timestamp
        );

        GameAchievementEvent result = GameAchievementEvent.convert(chessEvent, "chess");

        assertThat(result)
                .isNotNull();
    }

    @Test
    @DisplayName("should preserve achievement code during conversion")
    void testConversionPreservesAchievementCode() {
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        String achievementCode = "checkmate_in_5";
        LocalDateTime timestamp = LocalDateTime.now();

        ChessAchievementEvent chessEvent = new ChessAchievementEvent(
                gameId,
                playerId,
                "Player",
                achievementCode,
                "Description",
                ChessMessageType.ACHIEVEMENT_ACQUIRED,
                timestamp
        );

        GameAchievementEvent result = GameAchievementEvent.convert(chessEvent, "chess");

        assertThat(result.achievementCode()).isEqualTo(achievementCode);
    }

    @Test
    @DisplayName("should set game name from parameter")
    void testConversionSetsGameName() {
        ChessAchievementEvent chessEvent = new ChessAchievementEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Player",
                "achievement",
                "Description",
                ChessMessageType.ACHIEVEMENT_ACQUIRED,
                LocalDateTime.now()
        );

        GameAchievementEvent result = GameAchievementEvent.convert(chessEvent, "chess");

        assertThat(result.gameName()).isEqualTo("chess");
    }

    @Test
    @DisplayName("should preserve game id during conversion")
    void testConversionPreservesGameId() {
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        ChessAchievementEvent chessEvent = new ChessAchievementEvent(
                gameId,
                playerId,
                "Player",
                "achievement",
                "Description",
                ChessMessageType.ACHIEVEMENT_ACQUIRED,
                LocalDateTime.now()
        );

        GameAchievementEvent result = GameAchievementEvent.convert(chessEvent, "chess");

        assertThat(result.gameId()).isEqualTo(gameId);
    }

    @Test
    @DisplayName("should preserve player id during conversion")
    void testConversionPreservesPlayerId() {
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        ChessAchievementEvent chessEvent = new ChessAchievementEvent(
                gameId,
                playerId,
                "Player",
                "achievement",
                "Description",
                ChessMessageType.ACHIEVEMENT_ACQUIRED,
                LocalDateTime.now()
        );

        GameAchievementEvent result = GameAchievementEvent.convert(chessEvent, "chess");

        assertThat(result.playerId()).isEqualTo(playerId);
    }

    @Test
    @DisplayName("should convert local datetime to instant in UTC")
    void testConversionConvertsTimestampToInstant() {
        LocalDateTime timestamp = LocalDateTime.of(2025, 12, 31, 12, 0, 0);

        ChessAchievementEvent chessEvent = new ChessAchievementEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Player",
                "achievement",
                "Description",
                ChessMessageType.ACHIEVEMENT_ACQUIRED,
                timestamp
        );

        GameAchievementEvent result = GameAchievementEvent.convert(chessEvent, "chess");

        Instant expectedInstant = timestamp.toInstant(ZoneOffset.UTC);
        assertThat(result.occurredAt()).isEqualTo(expectedInstant);
    }

    @Test
    @DisplayName("should handle different game names")
    void testConversionWithDifferentGameNames() {
        ChessAchievementEvent chessEvent = new ChessAchievementEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Player",
                "achievement",
                "Description",
                ChessMessageType.ACHIEVEMENT_ACQUIRED,
                LocalDateTime.now()
        );

        GameAchievementEvent result1 = GameAchievementEvent.convert(chessEvent, "chess");
        GameAchievementEvent result2 = GameAchievementEvent.convert(chessEvent, "other-game");

        assertThat(result1.gameName()).isEqualTo("chess");
        assertThat(result2.gameName()).isEqualTo("other-game");
    }

    @Test
    @DisplayName("should preserve all data fields in conversion")
    void testConversionPreservesAllData() {
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        String achievementCode = "victory";
        String gameName = "chess";
        LocalDateTime timestamp = LocalDateTime.now();

        ChessAchievementEvent chessEvent = new ChessAchievementEvent(
                gameId,
                playerId,
                "PlayerName",
                achievementCode,
                "Win the game",
                ChessMessageType.ACHIEVEMENT_ACQUIRED,
                timestamp
        );

        GameAchievementEvent result = GameAchievementEvent.convert(chessEvent, gameName);

        assertThat(result)
                .extracting("achievementCode", "gameName", "gameId", "playerId")
                .containsExactly(achievementCode, gameName, gameId, playerId);
    }

    @Test
    @DisplayName("should handle special characters in achievement codes")
    void testConversionWithSpecialCharacterAchievementCode() {
        String achievementCode = "special-achievement_v2";

        ChessAchievementEvent chessEvent = new ChessAchievementEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Player",
                achievementCode,
                "Description",
                ChessMessageType.ACHIEVEMENT_ACQUIRED,
                LocalDateTime.now()
        );

        GameAchievementEvent result = GameAchievementEvent.convert(chessEvent, "chess");

        assertThat(result.achievementCode()).isEqualTo(achievementCode);
    }
}

package be.kdg.team22.gameaclservice.events.outbound;

import be.kdg.team22.gameaclservice.events.inbound.ChessGameEndedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GameEndedEvent")
class GameEndedEventTest {

    @Test
    @DisplayName("should convert chess game ended event to game ended event")
    void testConvertChessGameEndedToGameEndedEvent() {
        UUID gameId = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        ChessGameEndedEvent chessEvent = new ChessGameEndedEvent(
                gameId,
                "white_player",
                "black_player",
                "8/8/8/8/8/8/8/8",
                "checkmate",
                "white_player",
                42,
                "GAME_ENDED",
                timestamp
        );

        GameEndedEvent result = GameEndedEvent.convert(chessEvent);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("should preserve game id as instance id during conversion")
    void testConversionPreservesGameIdAsInstanceId() {
        UUID gameId = UUID.randomUUID();

        ChessGameEndedEvent chessEvent = new ChessGameEndedEvent(
                gameId,
                "white",
                "black",
                "fen",
                "checkmate",
                "white",
                10,
                "GAME_ENDED",
                LocalDateTime.now()
        );

        GameEndedEvent result = GameEndedEvent.convert(chessEvent);

        assertThat(result.instanceId()).isEqualTo(gameId);
    }

    @Test
    @DisplayName("should convert local datetime to instant in UTC")
    void testConversionConvertsTimestampToInstant() {
        LocalDateTime timestamp = LocalDateTime.of(2025, 12, 31, 14, 30, 45);

        ChessGameEndedEvent chessEvent = new ChessGameEndedEvent(
                UUID.randomUUID(),
                "white",
                "black",
                "fen",
                "checkmate",
                "white",
                50,
                "GAME_ENDED",
                timestamp
        );

        GameEndedEvent result = GameEndedEvent.convert(chessEvent);

        Instant expectedInstant = timestamp.toInstant(ZoneOffset.UTC);
        assertThat(result.occurredAt()).isEqualTo(expectedInstant);
    }

    @Test
    @DisplayName("should create instance with non-null fields")
    void testConversionCreatesCompleteEvent() {
        ChessGameEndedEvent chessEvent = new ChessGameEndedEvent(
                UUID.randomUUID(),
                "white_player",
                "black_player",
                "8/8/8/8/8/8/8/8",
                "resignation",
                "black_player",
                25,
                "GAME_ENDED",
                LocalDateTime.now()
        );

        GameEndedEvent result = GameEndedEvent.convert(chessEvent);

        assertThat(result.instanceId()).isNotNull();
        assertThat(result.occurredAt()).isNotNull();
    }

    @Test
    @DisplayName("should handle different game end reasons")
    void testConversionWithDifferentEndReasons() {
        String[] reasons = {"checkmate", "stalemate", "resignation", "timeout"};

        for (String reason : reasons) {
            ChessGameEndedEvent chessEvent = new ChessGameEndedEvent(
                    UUID.randomUUID(),
                    "white",
                    "black",
                    "fen",
                    reason,
                    "white",
                    30,
                    "GAME_ENDED",
                    LocalDateTime.now()
            );

            GameEndedEvent result = GameEndedEvent.convert(chessEvent);

            assertThat(result.instanceId()).isNotNull();
        }
    }

    @Test
    @DisplayName("should handle multiple game ids")
    void testConversionWithMultipleGameIds() {
        UUID gameId1 = UUID.randomUUID();
        UUID gameId2 = UUID.randomUUID();
        UUID gameId3 = UUID.randomUUID();

        ChessGameEndedEvent event1 = new ChessGameEndedEvent(
                gameId1, "w", "b", "f", "e", "w", 1, "GAME_ENDED", LocalDateTime.now());
        ChessGameEndedEvent event2 = new ChessGameEndedEvent(
                gameId2, "w", "b", "f", "e", "w", 1, "GAME_ENDED", LocalDateTime.now());
        ChessGameEndedEvent event3 = new ChessGameEndedEvent(
                gameId3, "w", "b", "f", "e", "w", 1, "GAME_ENDED", LocalDateTime.now());

        GameEndedEvent result1 = GameEndedEvent.convert(event1);
        GameEndedEvent result2 = GameEndedEvent.convert(event2);
        GameEndedEvent result3 = GameEndedEvent.convert(event3);

        assertThat(result1.instanceId()).isEqualTo(gameId1);
        assertThat(result2.instanceId()).isEqualTo(gameId2);
        assertThat(result3.instanceId()).isEqualTo(gameId3);
    }

    @Test
    @DisplayName("should preserve instance id regardless of other game details")
    void testConversionPreservesInstanceIdWithVaryingGameDetails() {
        UUID gameId = UUID.randomUUID();

        ChessGameEndedEvent event1 = new ChessGameEndedEvent(
                gameId, "white", "black", "fen1", "checkmate", "white", 50, "GAME_ENDED", LocalDateTime.now());
        ChessGameEndedEvent event2 = new ChessGameEndedEvent(
                gameId, "black", "white", "fen2", "resignation", "black", 10, "GAME_ENDED", LocalDateTime.now());

        GameEndedEvent result1 = GameEndedEvent.convert(event1);
        GameEndedEvent result2 = GameEndedEvent.convert(event2);

        assertThat(result1.instanceId()).isEqualTo(result2.instanceId()).isEqualTo(gameId);
    }

    @Test
    @DisplayName("should handle various move counts")
    void testConversionWithVariousMoveCounts() {
        UUID gameId = UUID.randomUUID();
        long[] moveCounts = {1, 5, 50, 100, 500};

        for (long moveCount : moveCounts) {
            ChessGameEndedEvent chessEvent = new ChessGameEndedEvent(
                    gameId,
                    "white",
                    "black",
                    "fen",
                    "checkmate",
                    "white",
                    moveCount,
                    "GAME_ENDED",
                    LocalDateTime.now()
            );

            GameEndedEvent result = GameEndedEvent.convert(chessEvent);

            assertThat(result.instanceId()).isEqualTo(gameId);
        }
    }

    @Test
    @DisplayName("should convert different timestamps correctly")
    void testConversionWithDifferentTimestamps() {
        UUID gameId = UUID.randomUUID();

        LocalDateTime timestamp1 = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime timestamp2 = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

        ChessGameEndedEvent event1 = new ChessGameEndedEvent(
                gameId, "w", "b", "f", "e", "w", 1, "GAME_ENDED", timestamp1);
        ChessGameEndedEvent event2 = new ChessGameEndedEvent(
                gameId, "w", "b", "f", "e", "w", 1, "GAME_ENDED", timestamp2);

        GameEndedEvent result1 = GameEndedEvent.convert(event1);
        GameEndedEvent result2 = GameEndedEvent.convert(event2);

        assertThat(result1.occurredAt()).isNotEqualTo(result2.occurredAt());
    }
}

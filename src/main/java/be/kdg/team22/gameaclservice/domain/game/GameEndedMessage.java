package be.kdg.team22.gameaclservice.domain.game;

import java.time.LocalDateTime;

public record GameEndedMessage(
        String gameId,
        String whitePlayer,
        String blackPlayer,
        String finalFeb,
        String endReason,
        String winner,
        int totalMoves,
        String messageType,
        LocalDateTime timestamp
) {
}

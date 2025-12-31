package be.kdg.team22.gameaclservice.events.inbound;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChessGameEndedEvent(
        UUID gameId,
        String whitePlayer,
        String blackPlayer,
        String finalFen,
        String endReason,
        String winner,
        long totalMoves,
        String messageType,
        LocalDateTime timestamp
) {
}

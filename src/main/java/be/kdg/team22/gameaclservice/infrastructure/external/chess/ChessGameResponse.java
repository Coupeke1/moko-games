package be.kdg.team22.gameaclservice.infrastructure.external.chess;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChessGameResponse(
        String status,
        String message,
        ChessGameData data

) {
    public record ChessGameData(
            UUID gameId,
            String currentFen,
            String status,
            UUID whitePlayerId,
            String whitePlayerName,
            UUID blackPlayerId,
            String blackPlayerName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {

    }
}

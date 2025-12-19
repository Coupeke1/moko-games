package be.kdg.team22.gameaclservice.infrastructure.external.chess;

import java.util.Map;
import java.util.UUID;

public record CreateChessGameRequest(
        UUID blackPlayerId,
        String blackPlayerName,
        UUID whitePlayerId,
        String whitePlayerName
) {
    public static CreateChessGameRequest convert(Map<UUID, String> users) {
        UUID blackPlayerId = users.keySet().stream().toList().get(0);
        UUID whitePlayerId = users.keySet().stream().toList().get(1);
        return new CreateChessGameRequest(
                blackPlayerId,
                users.get(blackPlayerId),
                whitePlayerId,
                users.get(whitePlayerId)
        );
    }
}

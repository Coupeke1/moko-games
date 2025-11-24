package be.kdg.team22.tictactoeservice.api.models;

import java.util.List;
import java.util.UUID;

public record CreateGameModel(
        UUID lobbyId,
        UUID gameId,
        List<UUID> players,
        GameSettingsModel settings
) {
}

package be.kdg.team22.gamesservice.api.game.models;

import java.util.List;
import java.util.UUID;

public record StartGameRequest(
        UUID lobbyId,
        UUID gameId,
        List<UUID> players,
        GameSettingsModel settings,
        boolean aiPlayer
) {
}
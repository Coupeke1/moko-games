package be.kdg.team22.tictactoeservice.api.models;

import java.util.List;
import java.util.UUID;

public record CreateGameModel(
        List<UUID> players,
        boolean hasBot,
        GameSettingsModel settings
) {
}

package be.kdg.team22.gamesservice.api.game.models;

public record CheckersSettingsModel(
        int boardSize,
        boolean flyingKings
) implements GameSettingsModel {
}
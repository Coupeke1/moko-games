package be.kdg.team22.gamesservice.api.game.models;

public record TicTacToeSettingsModel(
        int boardSize
) implements GameSettingsModel {
}
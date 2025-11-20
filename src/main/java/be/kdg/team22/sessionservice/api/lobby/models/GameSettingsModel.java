package be.kdg.team22.sessionservice.api.lobby.models;

public sealed interface GameSettingsModel
        permits TicTacToeSettingsModel, CheckersSettingsModel {
}

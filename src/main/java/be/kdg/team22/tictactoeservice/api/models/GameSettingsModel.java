package be.kdg.team22.tictactoeservice.api.models;

public record GameSettingsModel(int boardSize) {
    public GameSettingsModel {
        if (boardSize == 0) {
            boardSize = 3;
        }
    }
}

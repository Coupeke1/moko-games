package be.kdg.team22.tictactoeservice.api.models;

public record GameSettingsModel(String type, int boardSize) {
    public GameSettingsModel {
        if (boardSize == 0) {
            boardSize = 3;
        }
    }

    public GameSettingsModel(int boardSize) {
        this("tictactoe", boardSize);
    }
}

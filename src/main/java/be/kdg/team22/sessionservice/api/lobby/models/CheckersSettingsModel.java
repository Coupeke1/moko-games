package be.kdg.team22.sessionservice.api.lobby.models;

public record CheckersSettingsModel(int boardSize,
                                    boolean flyingKings) implements GameSettingsModel {}

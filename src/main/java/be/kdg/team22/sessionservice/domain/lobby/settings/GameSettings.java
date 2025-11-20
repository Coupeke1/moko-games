package be.kdg.team22.sessionservice.domain.lobby.settings;

public sealed interface GameSettings
        permits TicTacToeSettings, CheckersSettings {}

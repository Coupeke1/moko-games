package be.kdg.team22.tictactoeservice.api.models;

import be.kdg.team22.tictactoeservice.domain.Game;
import be.kdg.team22.tictactoeservice.domain.GameStatus;
import be.kdg.team22.tictactoeservice.domain.Player;
import be.kdg.team22.tictactoeservice.domain.PlayerRole;

import java.util.List;
import java.util.UUID;

public record GameModel(
        UUID id,
        PlayerRole[][] board,
        GameStatus status,
        List<Player> players,
        PlayerRole currentRole
) {
    public static GameModel fromDomain(Game game) {
        return new GameModel(
                game.getId().id(),
                game.getBoard().getGrid(),
                game.getStatus(),
                game.getPlayers(),
                game.getCurrentRole()
        );
    }
}

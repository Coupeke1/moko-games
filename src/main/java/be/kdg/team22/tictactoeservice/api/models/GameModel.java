package be.kdg.team22.tictactoeservice.api.models;

import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameStatus;
import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;

import java.util.TreeSet;
import java.util.UUID;

public record GameModel(
        UUID id,
        PlayerRole[][] board,
        GameStatus status,
        TreeSet<Player> players,
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

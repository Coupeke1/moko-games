package be.kdg.team22.tictactoeservice.api.models;

import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameStatus;
import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

public record GameModel(
        UUID id,
        PlayerRole[][] board,
        GameStatus status,
        TreeSet<Player> players,
        Map<UUID, List<MoveModel>> moveHistory,
        PlayerRole currentRole
) {
    public static GameModel from(Game game) {
        return new GameModel(
                game.id().value(),
                game.board().grid(),
                game.status(),
                game.players(),
                game.moveHistory().entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().value(),
                                entry -> entry.getValue().stream()
                                        .map(MoveModel::from).toList()
                        )),
                game.currentRole()
        );
    }
}

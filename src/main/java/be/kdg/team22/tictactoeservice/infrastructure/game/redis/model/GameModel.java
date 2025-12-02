package be.kdg.team22.tictactoeservice.infrastructure.game.redis.model;

import be.kdg.team22.tictactoeservice.domain.game.*;
import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameModel {
    public String id;
    public List<PlayerModel> players;
    public Map<String, List<MoveModel>> moveHistory;
    public PlayerRole aiPlayer;
    public int size;
    public PlayerRole[][] board;
    public GameStatus status;
    public PlayerRole currentRole;
    public String winner;

    public static GameModel fromDomain(Game game) {
        GameModel model = new GameModel();

        model.id = game.id().value().toString();
        model.players = game.players().stream()
                .map(PlayerModel::fromDomain)
                .toList();
        model.moveHistory = new HashMap<>();
        game.moveHistory().forEach((playerId, moves) -> model.moveHistory.put(
                playerId.value().toString(),
                moves.stream().map(MoveModel::fromDomain).toList()
        ));
        model.aiPlayer = game.aiPlayer();
        model.size = game.board().size();
        model.board = new PlayerRole[model.size][model.size];
        for (int r = 0; r < model.size; r++) {
            for (int c = 0; c < model.size; c++) {
                model.board[r][c] = game.board().cell(r, c);
            }
        }
        model.status = game.status();
        model.currentRole = game.currentRole();
        model.winner = game.winner() != null ? game.winner().value().toString() : null;

        return model;
    }

    public Game toDomain() {
        List<Player> domainPlayers = players.stream()
                .map(PlayerModel::toDomain)
                .toList();

        Map<PlayerId, List<Move>> domainMoves = new HashMap<>();
        moveHistory.forEach((playerId, moves) -> domainMoves.put(
                PlayerId.create(playerId),
                moves.stream().map(MoveModel::toDomain).toList()
        ));

        Board boardObj = Board.create(size);
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board[r][c] != null)
                    boardObj = boardObj.setCell(r, c, board[r][c]);
            }
        }

        return new Game(
                winner != null ? PlayerId.create(winner) : null,
                currentRole,
                status,
                boardObj,
                aiPlayer,
                domainMoves,
                domainPlayers,
                GameId.fromString(id)
        );
    }
}

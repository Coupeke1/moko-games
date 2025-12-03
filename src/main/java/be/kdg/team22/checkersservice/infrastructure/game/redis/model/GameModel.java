package be.kdg.team22.checkersservice.infrastructure.game.redis.model;

import be.kdg.team22.checkersservice.domain.board.Board;
import be.kdg.team22.checkersservice.domain.board.Piece;
import be.kdg.team22.checkersservice.domain.game.Game;
import be.kdg.team22.checkersservice.domain.game.GameId;
import be.kdg.team22.checkersservice.domain.game.GameStatus;
import be.kdg.team22.checkersservice.domain.player.Player;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;

import java.util.*;

public class GameModel {
    private String id;
    private List<PlayerModel> players;
    private PlayerRole aiPlayer;
    private PlayerRole currentRole;
    private int size;
    private Map<Integer, PieceModel> grid;
    private GameStatus status;

    public static GameModel fromDomain(Game game) {
        GameModel gameModel = new GameModel();
        gameModel.id = game.id().value().toString();
        gameModel.players = game.players().stream().map(PlayerModel::fromDomain).toList();
        gameModel.aiPlayer = game.aiPlayer();
        gameModel.currentRole = game.currentRole();
        gameModel.size = game.board().size();
        gameModel.grid = new HashMap<>();
        gameModel.status = game.status();

        game.board().grid().forEach((cellNumber, piece) -> {
            if (piece != null) {
                gameModel.grid.put(cellNumber, PieceModel.fromDomain(piece));
            }
        });

        return gameModel;
    }

    public Game toDomain() {
        TreeSet<Player> playerSet = new TreeSet<>(
                Comparator.comparing((Player p) -> p.role().order())
        );
        playerSet.addAll(players.stream()
                .map(PlayerModel::toDomain)
                .toList()
        );

        Map<Integer, Piece> reconstructedGrid = new HashMap<>();
        int totalCells = (size * size) / 2;

        for (int cell = 1; cell <= totalCells; cell++) {
            if (grid.containsKey(cell)) {
                reconstructedGrid.put(cell, grid.get(cell).toDomain());
            } else {
                reconstructedGrid.put(cell, null);
            }
        }

        Board board = new Board(size, reconstructedGrid);

        return new Game(GameId.fromString(id), playerSet, aiPlayer, currentRole, board, status);
    }
}

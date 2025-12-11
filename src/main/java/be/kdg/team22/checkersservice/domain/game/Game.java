package be.kdg.team22.checkersservice.domain.game;

import be.kdg.team22.checkersservice.domain.board.Board;
import be.kdg.team22.checkersservice.domain.move.KingMovementMode;
import be.kdg.team22.checkersservice.domain.move.Move;
import be.kdg.team22.checkersservice.domain.move.MoveResult;
import be.kdg.team22.checkersservice.domain.move.MoveValidator;
import be.kdg.team22.checkersservice.domain.game.exceptions.*;
import be.kdg.team22.checkersservice.domain.player.Player;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

@AggregateRoot
public class Game {
    private final GameId id;
    private final TreeSet<Player> players;
    private final PlayerRole botPlayer;
    private PlayerRole currentRole;
    private final Board board;
    private final KingMovementMode kingMovementMode;
    private GameStatus status;

    private Game(final List<Player> players, final PlayerRole botPlayer, final KingMovementMode kingMovementMode) {
        this.id = GameId.create();
        this.players = new TreeSet<>(Comparator.comparing((Player p) -> p.role().order()));
        this.players.addAll(players);
        this.botPlayer = botPlayer;
        this.currentRole = this.players.getFirst().role();
        this.board = Board.create(8);
        this.kingMovementMode = kingMovementMode;
        this.status = GameStatus.RUNNING;
    }

    public Game(final GameId id, final TreeSet<Player> players, final PlayerRole botPlayer, final PlayerRole currentRole, final Board board, final KingMovementMode kingMovementMode, final GameStatus status) {
        this.id = id;
        this.players = players;
        this.botPlayer = botPlayer;
        this.currentRole = currentRole;
        this.board = board;
        this.kingMovementMode = kingMovementMode;
        this.status = status;
    }

    public static Game create(final List<PlayerId> playerIds, final boolean botPlayer, final KingMovementMode kingMovementMode) {
        if (playerIds.size() != 2) {
            throw new PlayerCountException();
        }

        if (playerIds.get(0).equals(playerIds.get(1))) {
            throw new UniquePlayersException();
        }

        List<Player> players = new ArrayList<>();
        players.add(new Player(playerIds.getFirst(), PlayerRole.BLACK, false));
        players.add(new Player(playerIds.get(1), PlayerRole.WHITE, botPlayer));

        return new Game(players, botPlayer ? PlayerRole.WHITE : null, kingMovementMode);
    }

    public MoveResult requestMove(final Move move) {
        if (status != GameStatus.RUNNING) {
            throw new GameNotRunningException();
        }

        if (!currentPlayer().id().equals(move.playerId())) {
            throw new NotPlayersTurnException(currentRole);
        }

        MoveResult result = MoveValidator.move(board, currentRole, move, kingMovementMode);
        status = board.checkWinConditions();

        nextPlayer();
        return result;
    }

    public Player currentPlayer() {
        return players.stream()
                .filter(p -> p.role().equals(currentRole))
                .findFirst()
                .orElseThrow(() -> new RoleUnfulfilledException(currentRole));
    }

    private void nextPlayer() {
        currentRole = (currentRole == PlayerRole.BLACK) ? PlayerRole.WHITE : PlayerRole.BLACK;
    }

    public GameId id() {
        return id;
    }

    public TreeSet<Player> players() {
        return players;
    }

    public PlayerRole botPlayer() {
        return botPlayer;
    }

    public PlayerRole currentRole() {
        return currentRole;
    }

    public Board board() {
        return board;
    }

    public KingMovementMode kingMovementMode() {
        return kingMovementMode;
    }

    public GameStatus status() {
        return status;
    }

    public void reset() {
        if (status == GameStatus.RUNNING) throw new GameResetException();

        this.status = GameStatus.RUNNING;
        this.board.setupInitialPieces();
        this.currentRole = PlayerRole.BLACK;
    }

    public Player playerWithRole(final PlayerRole role) {
        return players.stream()
                .filter(p -> p.role() == role)
                .findFirst()
                .orElseThrow(() -> new RoleUnfulfilledException(role));
    }
}
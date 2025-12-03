package be.kdg.team22.checkersservice.domain.game;

import be.kdg.team22.checkersservice.domain.board.Board;
import be.kdg.team22.checkersservice.domain.board.Move;
import be.kdg.team22.checkersservice.domain.game.exceptions.PlayerCountException;
import be.kdg.team22.checkersservice.domain.game.exceptions.UniquePlayersException;
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
    private final PlayerRole aiPlayer;
    private PlayerRole currentRole;
    private final Board board;
    private GameStatus status;

    private Game(final List<Player> players, final PlayerRole aiPlayer) {
        this.id = GameId.create();
        this.players = new TreeSet<>(Comparator.comparing((Player p) -> p.role().order()));
        this.players.addAll(players);
        this.aiPlayer = aiPlayer;
        this.currentRole = this.players.getFirst().role();
        this.board = Board.create(8);
        this.status = GameStatus.RUNNING;
    }

    public static Game create(final List<PlayerId> playerIds, final boolean aiPlayer) {
        if (playerIds.size() != 2) {
            throw new PlayerCountException();
        }

        if (playerIds.get(0).equals(playerIds.get(1))) {
            throw new UniquePlayersException();
        }

        List<Player> players = new ArrayList<>();
        players.add(new Player(playerIds.getFirst(), PlayerRole.BLACK, false));
        players.add(new Player(playerIds.get(1), PlayerRole.WHITE, aiPlayer));

        return new Game(players, aiPlayer ? PlayerRole.WHITE : null);
    }

    public void requestMove(final Move move) {

    }

    public GameId id() {
        return id;
    }

    public TreeSet<Player> players() {
        return players;
    }

    public PlayerRole aiPlayer() {
        return aiPlayer;
    }

    public PlayerRole currentRole() {
        return currentRole;
    }

    public Board board() {
        return board;
    }

    public GameStatus status() {
        return status;
    }
}
package be.kdg.team22.tictactoeservice.domain.game;

import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AggregateRoot
public class Game {
    private final GameId id;
    private Board board;
    private GameStatus status;
    private final List<Player> players;
    private PlayerRole currentRole;

    private Game(int requestedSize, List<Player> players) {
        this.id = GameId.create();
        this.board = Board.create(requestedSize);
        this.status = GameStatus.IN_PROGRESS;
        this.players = players;
        this.currentRole = PlayerRole.X;
    }

    public static Game create(int minSize, int maxSize, int size, List<Player> players) {
        if (size < minSize || size > maxSize) {
            throw new IllegalArgumentException("Board size must be between " + minSize + " and " + maxSize);
        }

        if (players.size() < 2) {
            throw new IllegalArgumentException("There must be at least 2 players");
        }

        List<PlayerId> playerIds = players.stream().map(Player::id).toList();
        Set<PlayerId> uniquePlayerIds = new HashSet<>(playerIds);
        if (uniquePlayerIds.size() != players.size()) {
            throw new IllegalArgumentException("All players must be unique");
        }

        List<PlayerRole> playerRoles = players.stream().map(Player::role).toList();
        Set<PlayerRole> uniquePlayerRoles = new HashSet<>(playerRoles);
        if (uniquePlayerRoles.size() != players.size()) {
            throw new IllegalArgumentException("All players must have a different role");
        }

        return new Game(size, players);
    }

    public GameId getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public GameStatus getStatus() {
        return status;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public PlayerRole getCurrentRole() {
        return currentRole;
    }

    public void reset() {
        if (status == GameStatus.IN_PROGRESS)
            throw new IllegalStateException("Cannot reset GameStatus when status is IN_PROGRESS");

        this.status = GameStatus.IN_PROGRESS;
        this.board = Board.create(this.board.getSize());

        this.currentRole = PlayerRole.X;
    }
}

package be.kdg.team22.tictactoeservice.domain.game;

import be.kdg.team22.tictactoeservice.domain.NotFoundException;
import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.*;

@AggregateRoot
public class Game {
    private final GameId id;
    private Board board;
    private GameStatus status;
    private final TreeSet<Player> players;
    private PlayerRole currentRole;

    private Game(int requestedSize, List<Player> unsortedPlayers) {
        this.id = GameId.create();
        this.board = Board.create(requestedSize);
        this.status = GameStatus.IN_PROGRESS;
        this.players = new TreeSet<>(Comparator.comparing((Player player) ->
                player.role().order()));
        this.players.addAll(unsortedPlayers);
        this.currentRole = unsortedPlayers.getFirst().role();
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

        return new Game(size, players.stream()
                .sorted(Comparator.comparing(p -> p.role().order()))
                .toList());
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

    public TreeSet<Player> getPlayers() {
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

    public Player nextPlayer() {
        Player currentPlayer = players.stream()
                .filter(p -> p.role() == currentRole)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No player found with current role: " + currentRole));

        List<Player> playerList = new ArrayList<>(players);
        int currentIndex = playerList.indexOf(currentPlayer);
        int nextIndex = (currentIndex + 1) % playerList.size();

        Player nextPlayer = playerList.get(nextIndex);
        currentRole = nextPlayer.role();

        return nextPlayer;
    }
}

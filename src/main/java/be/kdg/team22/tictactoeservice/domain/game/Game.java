package be.kdg.team22.tictactoeservice.domain.game;

import be.kdg.team22.tictactoeservice.domain.game.exceptions.*;
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

    private Game(final int requestedSize, final List<Player> players) {
        this.id = GameId.create();
        this.board = Board.create(requestedSize);
        this.status = GameStatus.IN_PROGRESS;
        this.players = new TreeSet<>(Comparator.comparing((Player player) ->
                player.role().order()));
        this.players.addAll(players);
        this.currentRole = players.getFirst().role();
    }

    public static Game create(final int minSize, final int maxSize, final int size, final List<Player> players) {
        if (size < minSize || size > maxSize)
            throw new BoardSizeException(minSize, maxSize);

        if (players.size() < 2)
            throw new GameSizeException();

        List<PlayerId> playerIds = players.stream().map(Player::id).toList();
        Set<PlayerId> uniquePlayerIds = new HashSet<>(playerIds);
        if (uniquePlayerIds.size() != players.size())
            throw new UniquePlayersException();


        List<PlayerRole> playerRoles = players.stream().map(Player::role).toList();
        Set<PlayerRole> uniquePlayerRoles = new HashSet<>(playerRoles);
        if (uniquePlayerRoles.size() != players.size())
            throw new PlayerRolesException();

        return new Game(size, players);
    }

    public void reset() {
        if (status == GameStatus.IN_PROGRESS)
            throw new GameResetException();

        this.status = GameStatus.IN_PROGRESS;
        this.board = Board.create(this.board.size());

        this.currentRole = players.first().role();
    }

    public Player nextPlayer() {
        Player currentPlayer = players.stream()
                .filter(p -> p.role() == currentRole)
                .findFirst()
                .orElseThrow(() -> new RoleUnfulfilledException(currentRole));

        List<Player> playerList = new ArrayList<>(players);
        int currentIndex = playerList.indexOf(currentPlayer);
        int nextIndex = (currentIndex + 1) % playerList.size();

        Player nextPlayer = playerList.get(nextIndex);
        currentRole = nextPlayer.role();

        return nextPlayer;
    }

    public GameId id() {
        return id;
    }

    public Board board() {
        return board;
    }

    public GameStatus status() {
        return status;
    }

    public TreeSet<Player> players() {
        return players;
    }

    public PlayerRole currentRole() {
        return currentRole;
    }
}
package be.kdg.team22.tictactoeservice.domain.game;

import be.kdg.team22.tictactoeservice.domain.game.exceptions.*;
import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.*;
import java.util.stream.Collectors;

@AggregateRoot
public class Game {
    private final GameId id;
    private Board board;
    private GameStatus status;
    private final TreeSet<Player> players;
    private final Map<PlayerId, List<Move>> moveHistory;
    private PlayerRole currentRole;
    private PlayerId winner;

    private Game(final GameId id, final int requestedSize, final List<Player> players) {
        this.id = id;
        board = Board.create(requestedSize);
        status = GameStatus.IN_PROGRESS;
        this.players = new TreeSet<>(Comparator.comparing((Player player) ->
                player.role().order()));
        this.players.addAll(players);
        moveHistory = players.stream().collect(Collectors.toMap(Player::id, p -> new ArrayList<>()));
        currentRole = this.players.getFirst().role();
        winner = null;
    }

    public static Game create(final GameId id, final int minSize, final int maxSize, final int size, final List<PlayerId> playerIds) {
        if (size < minSize || size > maxSize)
            throw new BoardSizeException(minSize, maxSize);

        Set<PlayerId> uniquePlayerIds = new HashSet<>(playerIds);
        if (uniquePlayerIds.size() != playerIds.size())
            throw new UniquePlayersException();

        PlayerRole[] roles = PlayerRole.values();
        if (playerIds.size() < 2 || playerIds.size() > roles.length) {
            throw new GameSizeException(roles.length);
        }

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playerIds.size(); i++) {
            PlayerId playerId = playerIds.get(i);
            PlayerRole role = roles[i % roles.length];
            players.add(new Player(playerId, role));
        }

        return new Game(id, size, players);
    }

    public void reset() {
        if (status == GameStatus.IN_PROGRESS)
            throw new GameResetException();

        this.status = GameStatus.IN_PROGRESS;
        this.board = Board.create(this.board.size());

        this.currentRole = players.first().role();
    }

    public Player nextPlayer() {
        Player currentPlayer = currentPlayer();
        List<Player> playerList = new ArrayList<>(players);
        int currentIndex = playerList.indexOf(currentPlayer);

        int nextIndex = (currentIndex + 1) % playerList.size();
        Player nextPlayer = playerList.get(nextIndex);
        currentRole = nextPlayer.role();

        return nextPlayer;
    }

    public void requestMove(final Move move) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new GameNotInProgressException();
        }

        if (!currentPlayer().id().equals(move.playerId())) {
            throw new NotPlayersTurnException(currentPlayer().id().value());
        }

        if (move.row() < 0 || move.col() < 0
                || move.row() >= board.size() || move.col() >= board.size()) {
            throw new InvalidCellException(board.size());
        }

        if (board.cell(move.row(), move.col()) != null) {
            throw new CellOccupiedException(move.row(), move.col());
        }

        board = board.setCell(move.row(), move.col(), currentRole);
        moveHistory.get(move.playerId()).add(move);

        status = board.checkWinner(move.row(), move.col(), currentRole);
        if (status == GameStatus.WON) {
            winner = move.playerId();
        }

        if (status == GameStatus.IN_PROGRESS) {
            nextPlayer();
        }
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

    public Map<PlayerId, List<Move>> moveHistory() {
        return moveHistory;
    }

    public PlayerRole currentRole() {
        return currentRole;
    }

    public Player currentPlayer() {
        return players.stream()
                .filter(p -> p.role() == currentRole)
                .findFirst()
                .orElseThrow(() -> new RoleUnfulfilledException(currentRole));
    }

    public PlayerRole roleOfPlayer(PlayerId id) {
        return players.stream().filter(p -> p.id().equals(id))
                .map(Player::role)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(id.value()));
    }

    public PlayerId winner() {
        return winner;
    }
}
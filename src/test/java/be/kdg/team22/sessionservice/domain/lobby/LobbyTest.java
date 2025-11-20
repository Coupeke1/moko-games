package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.*;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LobbyTest {

    private GameId game(UUID id) {
        return GameId.from(id);
    }

    private PlayerId player(UUID id) {
        return PlayerId.from(id);
    }

    private LobbySettings defaultSettings() {
        return new LobbySettings(new TicTacToeSettings(3), 4);
    }

    @Test
    void constructor_createsLobbyWithOwnerAndOpenStatus() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());

        assertThat(lobby.gameId()).isEqualTo(gameId);
        assertThat(lobby.ownerId()).isEqualTo(owner);
        assertThat(lobby.status()).isEqualTo(LobbyStatus.OPEN);
        assertThat(lobby.players()).containsExactly(owner);
        assertThat(lobby.createdAt()).isNotNull();
        assertThat(lobby.updatedAt()).isNotNull();
    }

    @Test
    void allArgsConstructor_requiresOwnerToBePlayer() {
        LobbyId id = LobbyId.create();
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());
        Set<PlayerId> players = new LinkedHashSet<>();

        assertThatThrownBy(() -> new Lobby(
                id,
                gameId,
                owner,
                players,
                defaultSettings(),
                LobbyStatus.OPEN,
                Instant.now(),
                Instant.now()
        )).isInstanceOf(OwnerNotFoundException.class);
    }

    @Test
    void addPlayer_addsPlayer() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());
        PlayerId p2 = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());
        Instant before = lobby.updatedAt();

        lobby.addPlayer(p2);

        assertThat(lobby.players()).contains(owner, p2);
        assertThat(lobby.updatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void addPlayer_failsWhenMaxPlayersExceeded() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        LobbySettings smallSettings = new LobbySettings(new TicTacToeSettings(3), 1);
        Lobby lobby = new Lobby(gameId, owner, smallSettings);

        assertThatThrownBy(() -> lobby.addPlayer(player(UUID.randomUUID())))
                .isInstanceOf(MaxPlayersTooSmallException.class);
    }

    @Test
    void addPlayer_failsWhenClosed() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());
        lobby.start();

        assertThatThrownBy(() -> lobby.addPlayer(player(UUID.randomUUID())))
                .isInstanceOf(CannotJoinClosedLobbyException.class);
    }

    @Test
    void addPlayer_failsWhenAlreadyInLobby() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());

        assertThatThrownBy(() -> lobby.addPlayer(owner))
                .isInstanceOf(PlayerAlreadyInLobbyException.class);
    }

    @Test
    void removePlayer_removesPlayer() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());
        PlayerId p2 = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());
        lobby.addPlayer(p2);
        Instant before = lobby.updatedAt();

        lobby.removePlayer(p2);

        assertThat(lobby.players()).containsExactly(owner);
        assertThat(lobby.updatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void removePlayer_ownerCannotLeave() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());

        assertThatThrownBy(() -> lobby.removePlayer(owner))
                .isInstanceOf(OwnerCannotLeaveLobbyException.class);
    }

    @Test
    void removePlayer_failsWhenNotInLobby() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());
        PlayerId unknown = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());

        assertThatThrownBy(() -> lobby.removePlayer(unknown))
                .isInstanceOf(PlayerNotInLobbyException.class);
    }

    @Test
    void start_changesStatusToStarted() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());
        Instant before = lobby.updatedAt();

        lobby.start();

        assertThat(lobby.status()).isEqualTo(LobbyStatus.STARTED);
        assertThat(lobby.updatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void start_failsWhenAlreadyStarted() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());
        lobby.start();

        assertThatThrownBy(lobby::start)
                .isInstanceOf(LobbyAlreadyStartedException.class);
    }

    @Test
    void close_changesStatusToClosedAndUpdatesTimestamp() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());
        Instant before = lobby.updatedAt();

        lobby.close(owner);

        assertThat(lobby.status()).isEqualTo(LobbyStatus.CLOSED);
        assertThat(lobby.updatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void close_failsWhenNotOwner() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());
        PlayerId stranger = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());

        assertThatThrownBy(() -> lobby.close(stranger))
                .isInstanceOf(NotLobbyOwnerException.class);
    }

    @Test
    void changeSettings_updatesSettings() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());

        LobbySettings newSettings = new LobbySettings(
                new TicTacToeSettings(4),
                5
        );

        lobby.changeSettings(owner, newSettings);

        assertThat(lobby.settings()).isEqualTo(newSettings);
    }

    @Test
    void changeSettings_failsWhenTooManyPlayersForNewLimit() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());
        PlayerId p2 = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());
        lobby.addPlayer(p2);

        LobbySettings tooSmall = new LobbySettings(new TicTacToeSettings(3), 1);

        assertThatThrownBy(() -> lobby.changeSettings(owner, tooSmall))
                .isInstanceOf(MaxPlayersTooSmallException.class);
    }

    @Test
    void changeSettings_failsWhenNotOwner() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());
        PlayerId stranger = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());

        LobbySettings newSettings = new LobbySettings(new TicTacToeSettings(3), 5);

        assertThatThrownBy(() -> lobby.changeSettings(stranger, newSettings))
                .isInstanceOf(NotLobbyOwnerException.class);
    }

    @Test
    void changeSettings_failsWhenLobbyNotOpen() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());
        lobby.start();

        LobbySettings newSettings = new LobbySettings(new TicTacToeSettings(3), 5);

        assertThatThrownBy(() -> lobby.changeSettings(owner, newSettings))
                .isInstanceOf(LobbyManagementNotAllowedException.class);
    }

    @Test
    void players_returnsImmutableCopy() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner, defaultSettings());

        Set<PlayerId> result = lobby.players();

        assertThatThrownBy(() -> result.add(player(UUID.randomUUID())))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}

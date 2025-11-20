package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.domain.*;
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

    @Test
    void constructor_createsLobbyWithOwnerAndOpenStatus() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner);

        assertThat(lobby.gameId()).isEqualTo(gameId);
        assertThat(lobby.ownerId()).isEqualTo(owner);
        assertThat(lobby.status()).isEqualTo(LobbyStatus.OPEN);
        assertThat(lobby.players()).containsExactly(owner);
        assertThat(lobby.createdAt()).isNotNull();
        assertThat(lobby.updatedAt()).isNotNull();
    }

    @Test
    void allArgsConstructor_requiresOwnerToBePlayer() {
        LobbyId id = LobbyId.newId();
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());
        Set<PlayerId> players = new LinkedHashSet<>();

        assertThatThrownBy(() -> new Lobby(
                id,
                gameId,
                owner,
                players,
                LobbyStatus.OPEN,
                Instant.now(),
                Instant.now()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Owner must be part of players set");
    }

    @Test
    void addPlayer_addsPlayer() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());
        PlayerId p2 = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner);
        Instant before = lobby.updatedAt();

        lobby.addPlayer(p2);

        assertThat(lobby.players()).contains(owner, p2);
        assertThat(lobby.updatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void addPlayer_failsWhenClosed() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());
        PlayerId p2 = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner);
        lobby.start();

        assertThatThrownBy(() -> lobby.addPlayer(p2))
                .isInstanceOf(CannotJoinClosedLobbyException.class);
    }

    @Test
    void addPlayer_failsWhenAlreadyInLobby() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner);

        assertThatThrownBy(() -> lobby.addPlayer(owner))
                .isInstanceOf(PlayerAlreadyInLobbyException.class)
                .hasMessageContaining(owner.value().toString());
    }

    @Test
    void removePlayer_removesPlayer() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());
        PlayerId p2 = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner);
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

        Lobby lobby = new Lobby(gameId, owner);

        assertThatThrownBy(() -> lobby.removePlayer(owner))
                .isInstanceOf(OwnerCannotLeaveLobbyException.class)
                .hasMessageContaining(owner.value().toString());
    }

    @Test
    void removePlayer_failsWhenNotInLobby() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());
        PlayerId unknown = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner);

        assertThatThrownBy(() -> lobby.removePlayer(unknown))
                .isInstanceOf(PlayerNotInLobbyException.class)
                .hasMessageContaining(unknown.value().toString());
    }

    @Test
    void start_changesStatusAndUpdatesTimestamp() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner);
        Instant before = lobby.updatedAt();

        lobby.start();

        assertThat(lobby.status()).isEqualTo(LobbyStatus.STARTED);
        assertThat(lobby.updatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void start_failsWhenAlreadyStarted() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner);
        lobby.start();

        assertThatThrownBy(lobby::start)
                .isInstanceOf(LobbyAlreadyStartedException.class);
    }

    @Test
    void players_returnsImmutableCopy() {
        GameId gameId = game(UUID.randomUUID());
        PlayerId owner = player(UUID.randomUUID());
        PlayerId p2 = player(UUID.randomUUID());

        Lobby lobby = new Lobby(gameId, owner);
        lobby.addPlayer(p2);

        Set<PlayerId> copy = lobby.players();

        assertThat(copy).containsExactly(owner, p2);
        assertThatThrownBy(() -> copy.add(player(UUID.randomUUID())))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}

package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.*;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.PlayerName;
import be.kdg.team22.sessionservice.domain.player.exceptions.PlayerNotInLobbyException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LobbyTest {

    private GameId game() {
        return GameId.from(UUID.randomUUID());
    }

    private PlayerId pid() {
        return PlayerId.from(UUID.randomUUID());
    }

    private Player lp(PlayerId id, PlayerName username) {
        return new Player(id, username);
    }

    private LobbySettings settings(int max) {
        return new LobbySettings(new TicTacToeSettings(3), max);
    }

    @Test
    void constructor_createsLobbyWithOwner() {
        PlayerId owner = pid();
        Player ownerPlayer = lp(owner, new PlayerName("owner"));

        Lobby lobby = new Lobby(game(), ownerPlayer, settings(4));

        assertThat(lobby.ownerId()).isEqualTo(owner);
        assertThat(lobby.players()).hasSize(1);
        assertThat(lobby.players().iterator().next().id()).isEqualTo(owner);
        assertThat(lobby.status()).isEqualTo(LobbyStatus.OPEN);
    }

    @Test
    void fullConstructor_requiresOwnerInPlayers() {
        PlayerId owner = pid();

        List<Player> players = List.of(new Player(PlayerId.create(), new PlayerName("p1")));

        assertThatThrownBy(() -> new Lobby(LobbyId.create(), game(), owner, players, Set.of(), settings(4), LobbyStatus.OPEN, Instant.now(), Instant.now())).isInstanceOf(OwnerNotFoundException.class);
    }

    @Test
    void acceptInvite_addsPlayer() {
        PlayerId owner = pid();
        Player ownerPlayer = lp(owner, new PlayerName("owner"));

        Lobby lobby = new Lobby(game(), ownerPlayer, settings(4));

        PlayerId invited = pid();
        lobby.invitePlayer(owner, invited);

        Player invitedPlayer = lp(invited, new PlayerName("jan"));

        lobby.acceptInvite(invitedPlayer);

        assertThat(lobby.players().stream().map(Player::id)).contains(invited);
    }

    @Test
    void acceptInvite_throwsIfNotInvited() {
        Lobby lobby = new Lobby(game(), lp(pid(), new PlayerName("owner")), settings(4));
        Player someone = lp(pid(), new PlayerName("jimmy"));

        assertThatThrownBy(() -> lobby.acceptInvite(someone)).isInstanceOf(InviteNotFoundException.class);
    }

    @Test
    void acceptInvite_throwsIfFull() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(1));

        PlayerId invited = pid();
        lobby.invitePlayer(owner, invited);

        assertThatThrownBy(() -> lobby.acceptInvite(lp(invited, new PlayerName("p")))).isInstanceOf(LobbyFullException.class);
    }

    @Test
    void invitePlayer_ownerCanInvite() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));

        PlayerId invited = pid();
        lobby.invitePlayer(owner, invited);

        assertThat(lobby.isInvited(invited)).isTrue();
    }

    @Test
    void invitePlayer_nonOwnerThrows() {
        PlayerId owner = pid();
        PlayerId stranger = pid();

        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));
        PlayerId target = pid();

        assertThatThrownBy(() -> lobby.invitePlayer(stranger, target)).isInstanceOf(NotLobbyOwnerException.class);
    }

    @Test
    void removePlayer_ownerRemovesPlayer() {
        PlayerId owner = pid();
        PlayerId p2 = pid();

        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));
        lobby.invitePlayer(owner, p2);
        lobby.acceptInvite(lp(p2, new PlayerName("p2")));

        lobby.removePlayer(owner, p2);

        assertThat(lobby.players().stream().map(Player::id)).doesNotContain(p2);
    }

    @Test
    void removePlayer_cannotRemoveOwner() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));

        assertThatThrownBy(() -> lobby.removePlayer(owner, owner)).isInstanceOf(CannotRemoveOwnerException.class);
    }

    @Test
    void removePlayer_throwsIfPlayerNotInLobby() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));

        assertThatThrownBy(() -> lobby.removePlayer(owner, pid())).isInstanceOf(PlayerNotInLobbyException.class);
    }

    @Test
    void close_changesStatus() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));

        lobby.close(owner);

        assertThat(lobby.status()).isEqualTo(LobbyStatus.CLOSED);
    }

    @Test
    void close_nonOwnerThrows() {
        PlayerId owner = pid();
        PlayerId stranger = pid();

        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));

        assertThatThrownBy(() -> lobby.close(stranger)).isInstanceOf(NotLobbyOwnerException.class);
    }

    @Test
    void changeSettings_works() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));

        LobbySettings newSettings = new LobbySettings(new TicTacToeSettings(4), 5);

        lobby.changeSettings(owner, newSettings);

        assertThat(lobby.settings()).isEqualTo(newSettings);
    }

    @Test
    void changeSettings_throwsIfTooManyPlayers() {
        PlayerId owner = pid();
        PlayerId p2 = pid();

        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));

        lobby.invitePlayer(owner, p2);
        lobby.acceptInvite(lp(p2, new PlayerName("p2")));

        LobbySettings tooSmall = settings(1);

        assertThatThrownBy(() -> lobby.changeSettings(owner, tooSmall)).isInstanceOf(MaxPlayersTooSmallException.class);
    }

    @Test
    void players_returnsImmutableSet() {
        Lobby lobby = new Lobby(game(), lp(pid(), new PlayerName("owner")), settings(4));

        assertThatThrownBy(() -> lobby.players().add(lp(pid(), new PlayerName("x")))).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void setReady_setsReadyFlag() {
        PlayerId owner = pid();
        PlayerId p2 = pid();
        Player ownerPlayer = lp(owner, new PlayerName("owner"));
        Player p2Player = lp(p2, new PlayerName("john"));

        Lobby lobby = new Lobby(game(), ownerPlayer, settings(4));

        lobby.invitePlayer(owner, p2);
        lobby.acceptInvite(p2Player);

        lobby.setReady(p2);

        assertThat(lobby.players().stream().filter(p -> p.id().equals(p2)).findFirst().orElseThrow().ready()).isTrue();
    }

    @Test
    void setReady_throwsIfPlayerNotInLobby() {
        Lobby lobby = new Lobby(game(), lp(pid(), new PlayerName("owner")), settings(4));
        PlayerId notInLobby = pid();

        assertThatThrownBy(() -> lobby.setReady(notInLobby)).isInstanceOf(PlayerNotInLobbyException.class);
    }

    @Test
    void ensureAllPlayersReady_throwsIfAnyNotReady() {
        PlayerId owner = pid();
        Player ownerPlayer = lp(owner, new PlayerName("owner"));
        Lobby lobby = new Lobby(game(), ownerPlayer, settings(4));

        PlayerId p2 = pid();
        lobby.invitePlayer(owner, p2);
        lobby.acceptInvite(lp(p2, new PlayerName("p2")));

        lobby.setReady(owner);
        lobby.setUnready(p2);

        assertThatThrownBy(lobby::ensureAllPlayersReady).isInstanceOf(PlayersNotReadyException.class);
    }

    @Test
    void ensureAllPlayersReady_okWhenAllReady() {
        PlayerId owner = pid();
        Player ownerPlayer = lp(owner, new PlayerName("owner"));
        Lobby lobby = new Lobby(game(), ownerPlayer, settings(4));

        lobby.setReady(owner);
        lobby.ensureAllPlayersReady();
    }

    @Test
    void markStarted_setsStatusAndStoresGameId() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));

        GameId gameInstanceId = GameId.from(UUID.randomUUID());

        lobby.markStarted(gameInstanceId);

        assertThat(lobby.status()).isEqualTo(LobbyStatus.STARTED);
        assertThat(lobby.startedGameId()).contains(gameInstanceId);
    }

    @Test
    void markStarted_throwsWhenClosed() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));
        lobby.close(owner);

        assertThatThrownBy(() -> lobby.markStarted(GameId.from(UUID.randomUUID()))).isInstanceOf(LobbyStateInvalidException.class);
    }

    @Test
    void markStarted_throwsWhenAlreadyStarted() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));

        lobby.markStarted(GameId.from(UUID.randomUUID()));

        assertThatThrownBy(() -> lobby.markStarted(GameId.from(UUID.randomUUID()))).isInstanceOf(LobbyStateInvalidException.class);
    }

    @Test
    void invitePlayers_addsOnlyNonExistingPlayers() {
        PlayerId owner = pid();
        PlayerId p1 = pid();
        PlayerId p2 = pid();

        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));

        lobby.invitePlayers(owner, List.of(p1, p2));

        assertThat(lobby.invitedPlayers()).containsExactlyInAnyOrder(p1, p2);
    }

    @Test
    void invitePlayers_throwsForNonOwner() {
        PlayerId owner = pid();
        PlayerId notOwner = pid();

        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));

        assertThatThrownBy(() -> lobby.invitePlayers(notOwner, List.of(pid()))).isInstanceOf(NotLobbyOwnerException.class);
    }

    @Test
    void invitedPlayers_returnsImmutable() {
        Lobby lobby = new Lobby(game(), lp(pid(), new PlayerName("owner")), settings(4));

        assertThatThrownBy(() -> lobby.invitedPlayers().add(pid())).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void startedGameId_isEmptyBeforeStart() {
        Lobby lobby = new Lobby(game(), lp(pid(), new PlayerName("owner")), settings(4));
        assertThat(lobby.startedGameId()).isEmpty();
    }

    @Test
    void ensureModifiable_throwsWhenClosedOrStarted() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));

        lobby.close(owner);
        Lobby finalLobby = lobby;
        assertThatThrownBy(() -> finalLobby.invitePlayer(owner, pid())).isInstanceOf(LobbyStateInvalidException.class);

        lobby = new Lobby(game(), lp(owner, new PlayerName("owner")), settings(4));
        lobby.markStarted(GameId.from(UUID.randomUUID()));

        Lobby finalLobby1 = lobby;
        assertThatThrownBy(() -> finalLobby1.invitePlayer(owner, pid())).isInstanceOf(LobbyStateInvalidException.class);
    }
}

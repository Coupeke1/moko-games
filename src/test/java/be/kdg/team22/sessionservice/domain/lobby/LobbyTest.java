package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.*;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
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

    private Player lp(PlayerId id, String username, String email) {
        return new Player(id, username, email);
    }

    private LobbySettings settings(int max) {
        return new LobbySettings(new TicTacToeSettings(3), max);
    }

    @Test
    void constructor_createsLobbyWithOwner() {
        PlayerId owner = pid();
        Player ownerPlayer = lp(owner, "owner", "owner@email.com");

        Lobby lobby = new Lobby(game(), ownerPlayer, settings(4));

        assertThat(lobby.ownerId()).isEqualTo(owner);
        assertThat(lobby.players()).hasSize(1);
        assertThat(lobby.players().iterator().next().id()).isEqualTo(owner.value());
        assertThat(lobby.status()).isEqualTo(LobbyStatus.OPEN);
    }

    @Test
    void fullConstructor_requiresOwnerInPlayers() {
        PlayerId owner = pid();

        List<Player> players = List.of(new Player(PlayerId.create(), "p1", "p1@test.com"));

        assertThatThrownBy(() -> new Lobby(LobbyId.create(), game(), owner, players, Set.of(), settings(4), LobbyStatus.OPEN, Instant.now(), Instant.now())).isInstanceOf(OwnerNotFoundException.class);
    }

    @Test
    void acceptInvite_addsPlayer() {
        PlayerId owner = pid();
        Player ownerPlayer = lp(owner, "owner", "owner@email.com");

        Lobby lobby = new Lobby(game(), ownerPlayer, settings(4));

        PlayerId invited = pid();
        lobby.invitePlayer(owner, invited);

        Player invitedPlayer = lp(invited, "jan", "jan@email.com");

        lobby.acceptInvite(invitedPlayer);

        assertThat(lobby.players().stream().map(Player::id)).contains(invited);
    }

    @Test
    void acceptInvite_throwsIfNotInvited() {
        Lobby lobby = new Lobby(game(), lp(pid(), "owner", "owner@email.com"), settings(4));
        Player someone = lp(pid(), "jimmy", "jimmy@email.com");

        assertThatThrownBy(() -> lobby.acceptInvite(someone)).isInstanceOf(InviteNotFoundException.class);
    }

    @Test
    void acceptInvite_throwsIfFull() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, "owner", "owner@email.com"), settings(1));

        PlayerId invited = pid();
        lobby.invitePlayer(owner, invited);

        assertThatThrownBy(() -> lobby.acceptInvite(lp(invited, "p", "p@email.com"))).isInstanceOf(LobbyFullException.class);
    }

    @Test
    void invitePlayer_ownerCanInvite() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, "owner", "owner@email.com"), settings(4));

        PlayerId invited = pid();
        lobby.invitePlayer(owner, invited);

        assertThat(lobby.isInvited(invited)).isTrue();
    }

    @Test
    void invitePlayer_nonOwnerThrows() {
        PlayerId owner = pid();
        PlayerId stranger = pid();

        Lobby lobby = new Lobby(game(), lp(owner, "owner", "owner@email.com"), settings(4));
        PlayerId target = pid();

        assertThatThrownBy(() -> lobby.invitePlayer(stranger, target)).isInstanceOf(NotLobbyOwnerException.class);
    }

    @Test
    void removePlayer_ownerRemovesPlayer() {
        PlayerId owner = pid();
        PlayerId p2 = pid();

        Lobby lobby = new Lobby(game(), lp(owner, "owner", "owner@email.com"), settings(4));
        lobby.invitePlayer(owner, p2);
        lobby.acceptInvite(lp(p2, "p2", "p2@email.com"));

        lobby.removePlayer(owner, p2);

        assertThat(lobby.players().stream().map(Player::id)).doesNotContain(p2);
    }

    @Test
    void removePlayer_cannotRemoveOwner() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, "owner", "owner@email.com"), settings(4));

        assertThatThrownBy(() -> lobby.removePlayer(owner, owner)).isInstanceOf(CannotRemoveOwnerException.class);
    }

    @Test
    void removePlayer_throwsIfPlayerNotInLobby() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, "owner", "owner@email.com"), settings(4));

        assertThatThrownBy(() -> lobby.removePlayer(owner, pid())).isInstanceOf(PlayerNotInLobbyException.class);
    }

    @Test
    void close_changesStatus() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, "owner", "owner@email.com"), settings(4));

        lobby.close(owner);

        assertThat(lobby.status()).isEqualTo(LobbyStatus.CLOSED);
    }

    @Test
    void close_nonOwnerThrows() {
        PlayerId owner = pid();
        PlayerId stranger = pid();

        Lobby lobby = new Lobby(game(), lp(owner, "owner", "owner@email.com"), settings(4));

        assertThatThrownBy(() -> lobby.close(stranger)).isInstanceOf(NotLobbyOwnerException.class);
    }

    @Test
    void changeSettings_works() {
        PlayerId owner = pid();
        Lobby lobby = new Lobby(game(), lp(owner, "owner", "owner@email.com"), settings(4));

        LobbySettings newSettings = new LobbySettings(new TicTacToeSettings(4), 5);

        lobby.changeSettings(owner, newSettings);

        assertThat(lobby.settings()).isEqualTo(newSettings);
    }

    @Test
    void changeSettings_throwsIfTooManyPlayers() {
        PlayerId owner = pid();
        PlayerId p2 = pid();

        Lobby lobby = new Lobby(game(), lp(owner, "owner", "owner@email.com"), settings(4));

        lobby.invitePlayer(owner, p2);
        lobby.acceptInvite(lp(p2, "p2", "p2@email.com"));

        LobbySettings tooSmall = settings(1);

        assertThatThrownBy(() -> lobby.changeSettings(owner, tooSmall)).isInstanceOf(MaxPlayersTooSmallException.class);
    }

    @Test
    void players_returnsImmutableSet() {
        Lobby lobby = new Lobby(game(), lp(pid(), "owner", "owner@email.com"), settings(4));

        assertThatThrownBy(() -> lobby.players().add(lp(pid(), "x", "x@email.com"))).isInstanceOf(UnsupportedOperationException.class);
    }
}

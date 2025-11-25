package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.PlayerName;
import be.kdg.team22.sessionservice.infrastructure.player.ExternalPlayersRepository;
import be.kdg.team22.sessionservice.infrastructure.player.PlayerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyInviteQueryServiceTest {

    LobbyRepository lobbyRepo = mock(LobbyRepository.class);
    ExternalPlayersRepository userRepo = mock(ExternalPlayersRepository.class);

    LobbyInviteQueryService service = new LobbyInviteQueryService(lobbyRepo);

    @Test
    void getInvitesForUser_returnsEmptyList_whenNoLobbiesInvitePlayer() {
        PlayerId user = new PlayerId(UUID.randomUUID());

        when(lobbyRepo.findAll()).thenReturn(List.of());

        List<Lobby> result = service.getInvitesForPlayer(user);

        assertThat(result).isEmpty();
    }

    @Test
    void getInvitesForPlayer_returnsInvite_withCorrectMappedFields() {
        String token = "abc123";
        PlayerId invitedUser = new PlayerId(UUID.randomUUID());

        PlayerId ownerId = new PlayerId(UUID.randomUUID());
        Player owner = new Player(ownerId, new PlayerName("ownerUser"));

        Player p1 = new Player(PlayerId.create(), new PlayerName("p1"));
        Player p2 = new Player(PlayerId.create(), new PlayerName("p2"));

        PlayerId invited1 = new PlayerId(UUID.randomUUID());

        List<Player> players = List.of(owner, p1, p2);
        Set<PlayerId> invited = Set.of(invited1, invitedUser);

        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);

        Lobby lobby = new Lobby(
                LobbyId.create(),
                GameId.create(),
                ownerId,
                players,
                invited,
                settings,
                LobbyStatus.OPEN,
                Instant.now(),
                Instant.now()
        );

        when(lobbyRepo.findAll()).thenReturn(List.of(lobby));

        when(userRepo.getById(ownerId.value(), token))
                .thenReturn(Optional.of(new PlayerResponse(ownerId.value(), "ownerUser")));
        when(userRepo.getById(p1.id().value(), token))
                .thenReturn(Optional.of(new PlayerResponse(p1.id().value(), "p1")));
        when(userRepo.getById(p2.id().value(), token))
                .thenReturn(Optional.of(new PlayerResponse(p2.id().value(), "p2")));
        when(userRepo.getById(invited1.value(), token))
                .thenReturn(Optional.of(new PlayerResponse(invited1.value(), "inv1")));
        when(userRepo.getById(invitedUser.value(), token))
                .thenReturn(Optional.of(new PlayerResponse(invitedUser.value(), "inv2")));

        List<Lobby> result = service.getInvitesForPlayer(invitedUser);

        assertThat(result).hasSize(1);
    }

    @Test
    void getInvitesForUser_filtersOutLobbiesWherePlayerIsNotInvited() {
        PlayerId user = new PlayerId(UUID.randomUUID());

        Lobby invitedLobby = mock(Lobby.class);
        Lobby notInvitedLobby = mock(Lobby.class);

        when(invitedLobby.isInvited(user)).thenReturn(true);
        when(notInvitedLobby.isInvited(user)).thenReturn(false);
        when(lobbyRepo.findAll()).thenReturn(List.of(invitedLobby, notInvitedLobby));

        List<Lobby> result = service.getInvitesForPlayer(user);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(invitedLobby);

        verify(lobbyRepo).findAll();
        verify(invitedLobby).isInvited(user);
        verify(notInvitedLobby).isInvited(user);
    }
}
package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.LobbyInviteModel;
import be.kdg.team22.sessionservice.api.lobby.models.PlayerSummaryModel;
import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.users.ExternalUserRepository;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.users.UserResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LobbyInviteQueryServiceTest {

    LobbyRepository lobbyRepo = mock(LobbyRepository.class);
    ExternalUserRepository userRepo = mock(ExternalUserRepository.class);

    LobbyInviteQueryService service = new LobbyInviteQueryService(lobbyRepo, userRepo);

    @Test
    void getInvitesForUser_returnsEmptyList_whenNoLobbiesInviteUser() {
        PlayerId user = new PlayerId(UUID.randomUUID());

        when(lobbyRepo.findAll()).thenReturn(List.of());

        List<LobbyInviteModel> result = service.getInvitesForUser(user, "token");

        assertThat(result).isEmpty();
    }

    @Test
    void getInvitesForUser_returnsInvite_withCorrectMappedFields() {
        String token = "abc123";
        PlayerId invitedUser = new PlayerId(UUID.randomUUID());

        PlayerId owner = new PlayerId(UUID.randomUUID());

        LobbyPlayer ownerPlayer = new LobbyPlayer(owner.value(), "ownerUser");

        LobbyPlayer player1 = new LobbyPlayer(UUID.randomUUID(), "p1");
        LobbyPlayer player2 = new LobbyPlayer(UUID.randomUUID(), "p2");

        PlayerId invited1 = new PlayerId(UUID.randomUUID());

        Lobby lobby = new Lobby(
                new LobbyId(UUID.randomUUID()),
                new GameId(UUID.randomUUID()),
                owner,
                List.of(ownerPlayer, player1, player2),
                Set.of(invited1, invitedUser),
                new LobbySettings(new TicTacToeSettings(3), 4),
                LobbyStatus.OPEN,
                Instant.now(),
                Instant.now()
        );

        when(lobbyRepo.findAll()).thenReturn(List.of(lobby));

        when(userRepo.getById(owner.value(), token))
                .thenReturn(new UserResponse(owner.value(), "ownerUser", "owner@mail.com", Instant.now()));

        when(userRepo.getById(player1.id(), token))
                .thenReturn(new UserResponse(player1.id(), "p1", "p1@mail.com", Instant.now()));

        when(userRepo.getById(player2.id(), token))
                .thenReturn(new UserResponse(player2.id(), "p2", "p2@mail.com", Instant.now()));

        when(userRepo.getById(invited1.value(), token))
                .thenReturn(new UserResponse(invited1.value(), "inv1", "inv1@mail.com", Instant.now()));

        when(userRepo.getById(invitedUser.value(), token))
                .thenReturn(new UserResponse(invitedUser.value(), "inv2", "inv2@mail.com", Instant.now()));

        List<LobbyInviteModel> result = service.getInvitesForUser(invitedUser, token);

        assertThat(result).hasSize(1);

        LobbyInviteModel model = result.getFirst();

        assertThat(model.lobbyId()).isEqualTo(lobby.id().value());
        assertThat(model.gameId()).isEqualTo(lobby.gameId().value());
        assertThat(model.invitedById()).isEqualTo(owner.value());
        assertThat(model.invitedByUsername()).isEqualTo("ownerUser");

        assertThat(model.players())
                .extracting(PlayerSummaryModel::username)
                .containsExactlyInAnyOrder("ownerUser", "p1", "p2");

        assertThat(model.invited())
                .extracting(PlayerSummaryModel::username)
                .containsExactlyInAnyOrder("inv1", "inv2");

        assertThat(model.maxPlayers()).isEqualTo(4);
        assertThat(model.status()).isEqualTo("OPEN");

        verify(lobbyRepo).findAll();
        verify(userRepo, times(2)).getById(owner.value(), token);
        verify(userRepo).getById(player1.id(), token);
        verify(userRepo).getById(player2.id(), token);
        verify(userRepo).getById(invited1.value(), token);
        verify(userRepo).getById(invitedUser.value(), token);
    }

    @Test
    void getInvitesForUser_filtersOutLobbiesWhereUserIsNotInvited() {
        PlayerId user = new PlayerId(UUID.randomUUID());

        Lobby invitedLobby = mock(Lobby.class);
        Lobby notInvitedLobby = mock(Lobby.class);

        when(invitedLobby.isInvited(user)).thenReturn(true);
        when(notInvitedLobby.isInvited(user)).thenReturn(false);

        when(lobbyRepo.findAll()).thenReturn(List.of(invitedLobby, notInvitedLobby));

        LobbyId id = new LobbyId(UUID.randomUUID());
        PlayerId owner = new PlayerId(UUID.randomUUID());

        when(invitedLobby.ownerId()).thenReturn(owner);
        when(invitedLobby.players()).thenReturn(Set.of());
        when(invitedLobby.invitedPlayers()).thenReturn(Set.of());
        when(invitedLobby.settings()).thenReturn(new LobbySettings(new TicTacToeSettings(3), 4));
        when(invitedLobby.id()).thenReturn(id);
        when(invitedLobby.gameId()).thenReturn(new GameId(UUID.randomUUID()));
        when(invitedLobby.status()).thenReturn(LobbyStatus.OPEN);
        when(invitedLobby.createdAt()).thenReturn(Instant.now());

        when(userRepo.getById(any(), any()))
                .thenReturn(new UserResponse(UUID.randomUUID(), "dummy", "d@mail.com", Instant.now()));

        List<LobbyInviteModel> result = service.getInvitesForUser(user, "token");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isNotNull();
    }
}

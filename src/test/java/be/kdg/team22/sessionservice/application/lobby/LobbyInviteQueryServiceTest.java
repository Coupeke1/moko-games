package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.LobbyInviteModel;
import be.kdg.team22.sessionservice.api.lobby.models.PlayerSummaryModel;
import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.infrastructure.player.ExternalPlayerRepository;
import be.kdg.team22.sessionservice.infrastructure.player.PlayerResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LobbyInviteQueryServiceTest {
    LobbyRepository lobbyRepo = mock(LobbyRepository.class);
    ExternalPlayerRepository userRepo = mock(ExternalPlayerRepository.class);

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

        PlayerId owner = new PlayerId(UUID.randomUUID());

        Player ownerPlayer = new Player(owner, "ownerUser", "owner@email.com");

        Player player1 = new Player(PlayerId.create(), "p1", "p1@email.com");
        Player player2 = new Player(PlayerId.create(), "p2", "p2@email.com");

        PlayerId invited1 = new PlayerId(UUID.randomUUID());

        Lobby lobby = new Lobby(GameId.create(), ownerPlayer, null);

        when(userRepo.getById(player1.id().value(), token)).thenReturn(Optional.of(new PlayerResponse(player1.id().value(), "p1", "p1@mail.com")));

        when(userRepo.getById(player2.id().value(), token)).thenReturn(Optional.of(new PlayerResponse(player2.id().value(), "p2", "p2@mail.com")));

        when(userRepo.getById(invited1.value(), token)).thenReturn(Optional.of(new PlayerResponse(invited1.value(), "inv1", "inv1@mail.com")));

        when(userRepo.getById(invitedUser.value(), token)).thenReturn(Optional.of(new PlayerResponse(invitedUser.value(), "inv2", "inv2@mail.com")));

        List<Lobby> result = service.getInvitesForPlayer(invitedUser);

        assertThat(result).hasSize(1);

        LobbyInviteModel model = toInviteModel(result.getFirst(), token);

        assertThat(model.lobbyId()).isEqualTo(lobby.id().value());
        assertThat(model.gameId()).isEqualTo(lobby.gameId().value());
        assertThat(model.invitedById()).isEqualTo(owner.value());
        assertThat(model.invitedByUsername()).isEqualTo("ownerUser");

        assertThat(model.players()).extracting(PlayerSummaryModel::username).containsExactlyInAnyOrder("ownerUser", "p1", "p2");

        assertThat(model.invited()).extracting(PlayerSummaryModel::username).containsExactlyInAnyOrder("inv1", "inv2");

        assertThat(model.maxPlayers()).isEqualTo(4);
        assertThat(model.status()).isEqualTo("OPEN");

        verify(lobbyRepo).findAll();
        verify(userRepo, times(2)).getById(owner.value(), token);
        verify(userRepo).getById(player1.id().value(), token);
        verify(userRepo).getById(player2.id().value(), token);
        verify(userRepo).getById(invited1.value(), token);
        verify(userRepo).getById(invitedUser.value(), token);
    }

    @Test
    void getInvitesForUser_filtersOutLobbiesWherePlayerIsNotInvited() {
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

        when(userRepo.getById(any(), any())).thenReturn(Optional.of(new PlayerResponse(UUID.randomUUID(), "dummy", "d@mail.com")));

        List<Lobby> result = service.getInvitesForPlayer(user);
        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isNotNull();
    }

    private LobbyInviteModel toInviteModel(final Lobby lobby, final String token) {
        PlayerResponse owner = userRepo.getById(lobby.ownerId().value(), token).orElseThrow(lobby.ownerId()::notFound);
        Set<PlayerSummaryModel> players = lobby.players().stream().map(this::toPlayerModel).collect(Collectors.toSet());

        Set<PlayerSummaryModel> invited = lobby.invitedPlayers().stream().map(playerId -> {
            PlayerResponse player = userRepo.getById(playerId.value(), token).orElseThrow(playerId::notFound);
            return new PlayerSummaryModel(player.id(), player.username());
        }).collect(Collectors.toSet());

        return new LobbyInviteModel(lobby.id().value(), lobby.gameId().value(), "TODO_GAME_NAME", owner.id(), owner.username(), players, invited, lobby.settings().maxPlayers(), lobby.status().name(), lobby.createdAt());
    }

    private PlayerSummaryModel toPlayerModel(final Player player) {
        return new PlayerSummaryModel(player.id().value(), player.username());
    }
}

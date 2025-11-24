package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.application.friends.FriendsService;
import be.kdg.team22.sessionservice.application.player.PlayerService;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.PlayerName;
import be.kdg.team22.sessionservice.domain.player.exceptions.PlayerNotFriendException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyPlayerServiceTest {

    LobbyRepository repo = mock(LobbyRepository.class);
    FriendsService friendsService = mock(FriendsService.class);
    PlayerService playerService = mock(PlayerService.class);

    LobbyPlayerService service = new LobbyPlayerService(repo, friendsService, playerService);

    private Jwt jwtFor(PlayerId pid) {
        return Jwt.withTokenValue("TOKEN")
                .header("alg", "none")
                .claim("sub", pid.value().toString())
                .claim("preferred_username", "testuser")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    private Lobby mockLobby(LobbyId id, PlayerId owner) {
        Player ownerPlayer = new Player(owner, new PlayerName("owner"));
        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);
        return new Lobby(new GameId(UUID.randomUUID()), ownerPlayer, settings);
    }

    @Test
    void invitePlayer_success_whenFriend() {
        PlayerId owner = new PlayerId(UUID.randomUUID());
        PlayerId target = new PlayerId(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        Lobby lobby = mockLobby(lobbyId, owner);

        when(repo.findById(lobbyId)).thenReturn(Optional.of(lobby));
        when(friendsService.findAllFriends(any())).thenReturn(List.of(target));

        service.invitePlayer(owner, lobbyId, target, jwtFor(owner));

        verify(repo).save(lobby);
    }

    @Test
    void invitePlayer_notFriend_throws() {
        PlayerId owner = new PlayerId(UUID.randomUUID());
        PlayerId target = new PlayerId(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        Lobby lobby = mockLobby(lobbyId, owner);

        when(repo.findById(lobbyId)).thenReturn(Optional.of(lobby));
        when(friendsService.findAllFriends(any())).thenReturn(List.of());

        assertThatThrownBy(() ->
                service.invitePlayer(owner, lobbyId, target, jwtFor(owner))
        ).isInstanceOf(PlayerNotFriendException.class);
    }

    @Test
    void invitePlayer_lobbyNotFound_throws() {
        PlayerId owner = new PlayerId(UUID.randomUUID());
        PlayerId target = new PlayerId(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        when(repo.findById(lobbyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.invitePlayer(owner, lobbyId, target, jwtFor(owner))
        ).isInstanceOf(LobbyNotFoundException.class);
    }

    @Test
    void invitePlayers_success_whenAllAreFriends() {
        PlayerId owner = new PlayerId(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();
        List<PlayerId> targets = List.of(
                new PlayerId(UUID.randomUUID()),
                new PlayerId(UUID.randomUUID())
        );

        Lobby lobby = mockLobby(lobbyId, owner);

        when(repo.findById(lobbyId)).thenReturn(Optional.of(lobby));
        when(friendsService.findAllFriends(any())).thenReturn(targets);

        service.invitePlayers(owner, lobbyId, targets, jwtFor(owner));

        verify(repo).save(lobby);
    }

    @Test
    void invitePlayers_notAllFriends_throws() {
        PlayerId owner = new PlayerId(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        PlayerId friend = new PlayerId(UUID.randomUUID());
        PlayerId notFriend = new PlayerId(UUID.randomUUID());

        List<PlayerId> targets = List.of(friend, notFriend);

        Lobby lobby = mockLobby(lobbyId, owner);

        when(repo.findById(lobbyId)).thenReturn(Optional.of(lobby));
        when(friendsService.findAllFriends(any())).thenReturn(List.of(friend));

        assertThatThrownBy(() ->
                service.invitePlayers(owner, lobbyId, targets, jwtFor(owner))
        ).isInstanceOf(PlayerNotFriendException.class);
    }

    @Test
    void acceptInvite_success() {
        PlayerId playerId = new PlayerId(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        Lobby lobby = mockLobby(lobbyId, new PlayerId(UUID.randomUUID()));

        lobby.invitePlayer(lobby.ownerId(), playerId);

        when(repo.findById(lobbyId)).thenReturn(Optional.of(lobby));
        when(playerService.findPlayer(eq(playerId), any())).thenReturn(
                new Player(playerId, new PlayerName("player"))
        );

        service.acceptInvite(playerId, lobbyId, jwtFor(playerId));

        verify(repo).save(lobby);
    }

    @Test
    void acceptInvite_lobbyNotFound_throws() {
        PlayerId playerId = new PlayerId(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        when(repo.findById(lobbyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.acceptInvite(playerId, lobbyId, jwtFor(playerId))
        ).isInstanceOf(LobbyNotFoundException.class);
    }

    @Test
    void removePlayer_success() {
        PlayerId owner = new PlayerId(UUID.randomUUID());
        PlayerId target = new PlayerId(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        Lobby lobby = Mockito.spy(mockLobby(lobbyId, owner));

        lobby.invitePlayer(owner, target);
        lobby.acceptInvite(new Player(target, new PlayerName("target")));

        when(repo.findById(lobbyId)).thenReturn(Optional.of(lobby));

        service.removePlayer(owner, lobbyId, target);

        verify(lobby).removePlayer(owner, target);
        verify(repo).save(lobby);
    }

    @Test
    void removePlayers_success() {
        PlayerId owner = new PlayerId(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();
        List<PlayerId> ids = List.of(
                new PlayerId(UUID.randomUUID()),
                new PlayerId(UUID.randomUUID())
        );

        Lobby lobby = Mockito.spy(mockLobby(lobbyId, owner));

        when(repo.findById(lobbyId)).thenReturn(Optional.of(lobby));

        service.removePlayers(owner, lobbyId, ids);

        verify(lobby).removePlayers(owner, ids);
        verify(repo).save(lobby);
    }
}
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
import be.kdg.team22.sessionservice.infrastructure.lobby.LobbySocketPublisher;
import be.kdg.team22.sessionservice.infrastructure.messaging.LobbyEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private final LobbyRepository repo = mock(LobbyRepository.class);
    private final FriendsService friendsService = mock(FriendsService.class);
    private final PlayerService playerService = mock(PlayerService.class);
    private final LobbySocketPublisher socket = mock(LobbySocketPublisher.class);
    private final LobbyEventPublisher eventPublisher = mock(LobbyEventPublisher.class);
    private final LobbyPublisherService publisherService = new LobbyPublisherService(repo, socket);
    LobbyPlayerService service = new LobbyPlayerService(repo, friendsService, playerService, publisherService, eventPublisher);

    private Jwt jwtFor(PlayerId pid) {
        return Jwt.withTokenValue("TOKEN-" + pid.value()).header("alg", "none").claim("sub", pid.value().toString()).claim("preferred_username", "testuser").issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3600)).build();
    }

    private Lobby newLobby(LobbyId id, PlayerId owner) {
        Player ownerPlayer = new Player(owner, new PlayerName("owner"), "");
        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);
        return new Lobby(new GameId(UUID.randomUUID()), ownerPlayer, settings);
    }

    @Test
    void invitePlayer_success() {
        PlayerId owner = PlayerId.from(UUID.randomUUID());
        PlayerId target = PlayerId.from(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        Lobby lobby = newLobby(lobbyId, owner);
        Player ownerPlayer = new Player(owner, new PlayerName("owner"), "");

        when(repo.findById(lobbyId)).thenReturn(Optional.of(lobby));
        when(friendsService.findAllFriends(any())).thenReturn(List.of(target));
        when(playerService.findPlayer(eq(owner), any())).thenReturn(ownerPlayer);

        service.invitePlayer(owner, lobbyId, target, jwtFor(owner));

        verify(repo).save(lobby);
    }

    @Test
    void invitePlayer_notFriend_throws() {
        PlayerId owner = PlayerId.from(UUID.randomUUID());
        PlayerId target = PlayerId.from(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        Lobby lobby = newLobby(lobbyId, owner);

        when(repo.findById(lobbyId)).thenReturn(Optional.of(lobby));
        when(friendsService.findAllFriends(any())).thenReturn(List.of());

        assertThatThrownBy(() -> service.invitePlayer(owner, lobbyId, target, jwtFor(owner))).isInstanceOf(PlayerNotFriendException.class);
    }

    @Test
    void invitePlayer_lobbyNotFound_throws() {
        PlayerId owner = PlayerId.from(UUID.randomUUID());
        PlayerId target = PlayerId.from(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        when(repo.findById(lobbyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.invitePlayer(owner, lobbyId, target, jwtFor(owner))).isInstanceOf(LobbyNotFoundException.class);
    }

    @Test
    void acceptInvite_success() {
        PlayerId player = PlayerId.from(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();
        Player p = new Player(player, new PlayerName("test"), "");

        Lobby lobby = newLobby(lobbyId, PlayerId.from(UUID.randomUUID()));

        lobby.invitePlayer(lobby.ownerId(), player);

        when(repo.findById(lobbyId)).thenReturn(Optional.of(lobby));
        when(playerService.findPlayer(eq(player), any())).thenReturn(p);

        service.acceptInvite(player, lobbyId, jwtFor(player));

        verify(repo).save(lobby);
    }

    @Test
    void acceptInvite_lobbyNotFound_throws() {
        PlayerId player = PlayerId.from(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        when(repo.findById(lobbyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.acceptInvite(player, lobbyId, jwtFor(player))).isInstanceOf(LobbyNotFoundException.class);
    }

    @Test
    void removePlayer_success() {
        PlayerId owner = PlayerId.from(UUID.randomUUID());
        PlayerId target = PlayerId.from(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        Lobby lobby = spy(newLobby(lobbyId, owner));

        lobby.invitePlayer(owner, target);
        lobby.acceptInvite(new Player(target, new PlayerName("target"), ""));

        when(repo.findById(lobbyId)).thenReturn(Optional.of(lobby));

        service.removePlayer(owner, lobbyId, target);

        verify(lobby).removePlayer(owner, target);
        verify(repo).save(lobby);
    }

    @Test
    void removePlayer_lobbyNotFound_throws() {
        PlayerId owner = PlayerId.from(UUID.randomUUID());
        PlayerId target = PlayerId.from(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        when(repo.findById(lobbyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removePlayer(owner, lobbyId, target)).isInstanceOf(LobbyNotFoundException.class);
    }

    @Test
    void setReady_success() {
        PlayerId p = PlayerId.from(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        Lobby lobby = spy(newLobby(lobbyId, p));

        when(repo.findById(lobbyId)).thenReturn(Optional.of(lobby));

        service.setReady(p, lobbyId);

        verify(lobby).setReady(p);
        verify(repo).save(lobby);
    }

    @Test
    void setReady_lobbyNotFound_throws() {
        PlayerId p = PlayerId.from(UUID.randomUUID());
        LobbyId lobbyId = LobbyId.create();

        when(repo.findById(lobbyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setReady(p, lobbyId)).isInstanceOf(LobbyNotFoundException.class);
    }
}
package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.CreateLobbyModel;
import be.kdg.team22.sessionservice.application.player.PlayerService;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.GameNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.NotLobbyOwnerException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.PlayersNotReadyException;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.PlayerName;
import be.kdg.team22.sessionservice.infrastructure.chat.ExternalChatRepository;
import be.kdg.team22.sessionservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.sessionservice.infrastructure.games.StartGameResponse;
import be.kdg.team22.sessionservice.infrastructure.lobby.LobbyPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyServiceTest {
    private final LobbyRepository repo = mock(LobbyRepository.class);
    private final PlayerService playerService = mock(PlayerService.class);
    private final ExternalGamesRepository gameClient = mock(ExternalGamesRepository.class);
    private final LobbyPublisher socket = mock(LobbyPublisher.class);
    private final ExternalChatRepository chat = mock(ExternalChatRepository.class);
    private final PublisherService publisherService = new PublisherService(repo, socket);
    private final LobbyService service = new LobbyService(repo, publisherService, playerService, gameClient, chat);
    private final LobbySettings settings = new LobbySettings(
            2,
            Map.of("boardSize", 3)
    );

    private Jwt jwtFor(PlayerId owner) {
        return Jwt.withTokenValue("TOKEN-" + owner.value())
                .header("alg", "none")
                .header("typ", "JWT")
                .claim("sub", owner.value().toString())
                .claim("preferred_username", "owner")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    private Lobby newLobby(GameId gameId, PlayerId owner) {
        Player p = new Player(owner, new PlayerName("owner"), "");
        return new Lobby(gameId, p, settings);
    }

    @Test
    void findLobby_returnsLobby() {
        LobbyId id = LobbyId.create();
        Lobby lobby = mock(Lobby.class);

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        assertThat(service.findLobby(id)).isEqualTo(lobby);
    }

    @Test
    void findLobby_notFound_throws() {
        LobbyId id = LobbyId.create();
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findLobby(id)).isInstanceOf(LobbyNotFoundException.class);
    }

    @Test
    void findAll_returnsList() {
        List<Lobby> list = List.of(mock(Lobby.class));
        when(repo.findAll()).thenReturn(list);

        assertThat(service.findAllLobbies()).containsExactlyElementsOf(list);
    }

    @Test
    void closeLobby_succeeds() {
        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());

        Lobby lobby = spy(newLobby(new GameId(UUID.randomUUID()), owner));
        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        Lobby closed = service.closeLobby(id, owner);

        verify(lobby).close(owner);
        verify(repo).save(lobby);
        assertThat(closed).isEqualTo(lobby);
    }

    @Test
    void startLobby_succeeds() {
        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());

        Lobby lobby = spy(newLobby(new GameId(UUID.randomUUID()), owner));

        Player ownerPlayer = new Player(owner, new PlayerName("owner"), "");
        Player p2 = new Player(PlayerId.create(), new PlayerName("player2"), "");

        when(lobby.players()).thenReturn(Set.of(ownerPlayer, p2));
        doNothing().when(lobby).ensureOwner(owner);
        doNothing().when(lobby).ensureAllPlayersReady();
        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        UUID newGameInstance = UUID.randomUUID();
        StartGameResponse response = new StartGameResponse(newGameInstance, "title", "game", "url", "frontendurl");

        when(gameClient.startGame(any(), any())).thenReturn(response);
        when(lobby.size()).thenReturn(2L);
        when(lobby.hasBot()).thenReturn(false);

        Lobby out = service.startLobby(id, owner, jwtFor(owner));

        verify(gameClient).startGame(any(), any());
        verify(lobby).markStarted(GameId.from(newGameInstance));
        verify(repo).save(lobby);

        assertThat(out).isEqualTo(lobby);
    }

    @Test
    void startLobby_notOwner_throws() {
        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());
        PlayerId other = new PlayerId(UUID.randomUUID());

        Lobby lobby = spy(newLobby(new GameId(UUID.randomUUID()), owner));
        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        doThrow(new NotLobbyOwnerException(other)).when(lobby).ensureOwner(other);

        assertThatThrownBy(() -> service.startLobby(id, other, jwtFor(other)))
                .isInstanceOf(NotLobbyOwnerException.class);
    }

    @Test
    void startLobby_playersNotReady_throws() {
        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());

        Lobby lobby = spy(newLobby(new GameId(UUID.randomUUID()), owner));
        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        doNothing().when(lobby).ensureOwner(owner);
        doThrow(new PlayersNotReadyException(id.value())).when(lobby).ensureAllPlayersReady();

        assertThatThrownBy(() -> service.startLobby(id, owner, jwtFor(owner)))
                .isInstanceOf(PlayersNotReadyException.class);
    }

    @Test
    void startLobby_gameClientThrows_propagates() {
        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());

        Lobby lobby = spy(newLobby(new GameId(UUID.randomUUID()), owner));

        Player ownerPlayer = new Player(owner, new PlayerName("owner"), "");
        Player p2 = new Player(PlayerId.create(), new PlayerName("player2"), "");

        when(lobby.players()).thenReturn(Set.of(ownerPlayer, p2));
        when(lobby.size()).thenReturn(2L);
        when(lobby.hasBot()).thenReturn(false);

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        doNothing().when(lobby).ensureOwner(owner);
        doNothing().when(lobby).ensureAllPlayersReady();

        when(gameClient.startGame(any(), any()))
                .thenThrow(new GameNotFoundException(UUID.randomUUID()));

        assertThatThrownBy(() -> service.startLobby(id, owner, jwtFor(owner)))
                .isInstanceOf(GameNotFoundException.class);
    }

    @Test
    void createLobby_defaultsMaxPlayersTo2() {
        GameId gameId = new GameId(UUID.randomUUID());
        PlayerId owner = new PlayerId(UUID.randomUUID());
        Jwt token = jwtFor(owner);

        when(gameClient.validateAndResolveSettings(gameId, Map.of(), token)).thenReturn(Map.of("boardSize", 3));
        when(playerService.findPlayer(owner, token)).thenReturn(new Player(owner, new PlayerName("owner"), ""));

        CreateLobbyModel model = new CreateLobbyModel(gameId.value(), null, null);

        Lobby lobby = service.createLobby(gameId, owner, model, token);

        assertThat(lobby.settings().maxPlayers()).isEqualTo(2); // pas aan als jij default 4 wil
    }
}
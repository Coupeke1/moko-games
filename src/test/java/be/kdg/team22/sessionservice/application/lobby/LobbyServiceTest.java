package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.CheckersSettingsModel;
import be.kdg.team22.sessionservice.api.lobby.models.CreateLobbyModel;
import be.kdg.team22.sessionservice.api.lobby.models.TicTacToeSettingsModel;
import be.kdg.team22.sessionservice.api.lobby.models.UpdateLobbySettingsModel;
import be.kdg.team22.sessionservice.application.player.PlayerService;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.GameNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.NotLobbyOwnerException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.PlayersNotReadyException;
import be.kdg.team22.sessionservice.domain.lobby.settings.CheckersSettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.PlayerName;
import be.kdg.team22.sessionservice.infrastructure.games.GameClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyServiceTest {

    LobbyRepository repo = mock(LobbyRepository.class);
    PlayerService playerService = mock(PlayerService.class);
    GameClient gameClient = mock(GameClient.class);

    LobbyService service = new LobbyService(repo, playerService, gameClient);

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
        Player p = new Player(owner, new PlayerName("owner"));
        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);
        return new Lobby(gameId, p, settings);
    }

    // ------------------------------------------------------------------
    // createLobby
    // ------------------------------------------------------------------

    @Test
    void createLobby_savesLobby_andReturnsIt() {
        GameId gameId = new GameId(UUID.randomUUID());
        PlayerId owner = new PlayerId(UUID.randomUUID());
        Jwt token = jwtFor(owner);

        CreateLobbyModel model =
                new CreateLobbyModel(gameId.value(), 4, new TicTacToeSettingsModel(3));

        when(playerService.findPlayer(owner, token))
                .thenReturn(new Player(owner, new PlayerName("owner")));

        Lobby l = service.createLobby(gameId, owner, model, token);

        assertThat(l.gameId()).isEqualTo(gameId);
        assertThat(l.ownerId()).isEqualTo(owner);
        assertThat(l.settings().maxPlayers()).isEqualTo(4);
        assertThat(l.settings().gameSettings()).isInstanceOf(TicTacToeSettings.class);

        verify(repo).save(any(Lobby.class));
    }

    // ------------------------------------------------------------------
    // findLobby
    // ------------------------------------------------------------------

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

        assertThatThrownBy(() -> service.findLobby(id))
                .isInstanceOf(LobbyNotFoundException.class);
    }

    // ------------------------------------------------------------------
    // findAllLobbies
    // ------------------------------------------------------------------

    @Test
    void findAll_returnsList() {
        List<Lobby> list = List.of(mock(Lobby.class));
        when(repo.findAll()).thenReturn(list);

        assertThat(service.findAllLobbies()).containsExactlyElementsOf(list);
    }

    // ------------------------------------------------------------------
    // updateSettings
    // ------------------------------------------------------------------

    @Test
    void updateSettings_tictactoe_updatesSuccessfully() {
        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());

        Lobby lobby = newLobby(new GameId(UUID.randomUUID()), owner);

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        UpdateLobbySettingsModel model =
                new UpdateLobbySettingsModel(5, new TicTacToeSettingsModel(5));

        Lobby updated = service.updateSettings(id, owner, model);

        assertThat(updated.settings().maxPlayers()).isEqualTo(5);
        assertThat(updated.settings().gameSettings()).isInstanceOf(TicTacToeSettings.class);
        assertThat(((TicTacToeSettings) updated.settings().gameSettings()).boardSize())
                .isEqualTo(5);

        verify(repo).save(lobby);
    }

    @Test
    void updateSettings_checkers_updatesSuccessfully() {
        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());

        Lobby lobby = newLobby(new GameId(UUID.randomUUID()), owner);

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        UpdateLobbySettingsModel model =
                new UpdateLobbySettingsModel(6, new CheckersSettingsModel(8, true));

        Lobby updated = service.updateSettings(id, owner, model);

        assertThat(updated.settings().maxPlayers()).isEqualTo(6);
        assertThat(updated.settings().gameSettings()).isInstanceOf(CheckersSettings.class);

        verify(repo).save(lobby);
    }

    @Test
    void updateSettings_nullSettings_throws() {
        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());

        Lobby lobby = newLobby(new GameId(UUID.randomUUID()), owner);

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        UpdateLobbySettingsModel model = new UpdateLobbySettingsModel(4, null);

        assertThatThrownBy(() -> service.updateSettings(id, owner, model))
                .isInstanceOf(NullPointerException.class);
    }

    // ------------------------------------------------------------------
    // closeLobby
    // ------------------------------------------------------------------

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

    // ------------------------------------------------------------------
    // startLobby
    // ------------------------------------------------------------------

    @Test
    void startLobby_succeeds() {
        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());

        Lobby lobby = spy(newLobby(new GameId(UUID.randomUUID()), owner));

        // Mock ready state
        doNothing().when(lobby).ensureOwner(owner);
        doNothing().when(lobby).ensureAllPlayersReady();

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        UUID newGameInstance = UUID.randomUUID();
        GameClient.StartGameResponse response =
                new GameClient.StartGameResponse(newGameInstance);

        when(gameClient.startGame(any(), any())).thenReturn(response);

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

        doThrow(new NotLobbyOwnerException(other))
                .when(lobby).ensureOwner(other);

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
        doThrow(new PlayersNotReadyException(id.value()))
                .when(lobby).ensureAllPlayersReady();

        assertThatThrownBy(() -> service.startLobby(id, owner, jwtFor(owner)))
                .isInstanceOf(PlayersNotReadyException.class);
    }

    @Test
    void startLobby_gameClientThrows_propagates() {
        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());

        Lobby lobby = spy(newLobby(new GameId(UUID.randomUUID()), owner));

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        doNothing().when(lobby).ensureOwner(owner);
        doNothing().when(lobby).ensureAllPlayersReady();

        when(gameClient.startGame(any(), any()))
                .thenThrow(new GameNotFoundException(UUID.randomUUID()));

        assertThatThrownBy(() -> service.startLobby(id, owner, jwtFor(owner)))
                .isInstanceOf(GameNotFoundException.class);
    }

    @Test
    void createLobby_defaultsMaxPlayersTo4() {
        GameId gameId = new GameId(UUID.randomUUID());
        PlayerId owner = new PlayerId(UUID.randomUUID());
        Jwt token = jwtFor(owner);

        CreateLobbyModel model = new CreateLobbyModel(
                gameId.value(),
                null,
                new TicTacToeSettingsModel(3)
        );

        when(playerService.findPlayer(owner, token))
                .thenReturn(new Player(owner, new PlayerName("owner")));

        doNothing().when(repo).save(any());

        Lobby lobby = service.createLobby(gameId, owner, model, token);

        assertThat(lobby.settings().maxPlayers()).isEqualTo(4);
    }
}
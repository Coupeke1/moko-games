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
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.settings.CheckersSettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.PlayerName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class LobbyServiceTest {
    LobbyRepository repo = mock(LobbyRepository.class);
    PlayerService playerService = mock(PlayerService.class);

    LobbyService service = new LobbyService(repo, playerService);

    @Test
    void createLobby_savesLobby_andReturnsIt() {

        GameId gameId = new GameId(UUID.randomUUID());
        PlayerId owner = new PlayerId(UUID.randomUUID());

        CreateLobbyModel model = new CreateLobbyModel(gameId.value(), 4, new TicTacToeSettingsModel(3));

        when(playerService.findPlayer(owner, Jwt.withTokenValue("TOKEN").build())).thenReturn(new Player(owner, new PlayerName("owner")));

        doNothing().when(repo).save(any(Lobby.class));

        Lobby lobby = service.createLobby(gameId, owner, model, Jwt.withTokenValue("TOKEN").build());

        assertThat(lobby.gameId()).isEqualTo(gameId);
        assertThat(lobby.ownerId().value()).isEqualTo(owner.value());
        assertThat(lobby.settings().maxPlayers()).isEqualTo(4);
        assertThat(lobby.settings().gameSettings()).isInstanceOf(TicTacToeSettings.class);

        verify(repo).save(any(Lobby.class));
        verify(playerService).findPlayer(owner, Jwt.withTokenValue("TOKEN").build());
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
    void findAllLobbies_returnsList() {

        List<Lobby> list = List.of(mock(Lobby.class));
        when(repo.findAll()).thenReturn(list);

        assertThat(service.findAllLobbies()).containsExactlyElementsOf(list);
    }

    private Lobby existingLobby(LobbyId id, PlayerId owner) {
        Player player = new Player(owner, new PlayerName("owner"));
        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);

        return new Lobby(new GameId(UUID.randomUUID()), player, settings);
    }

    @Test
    void updateSettings_tictactoe_updatesSuccessfully() {
        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());
        Lobby lobby = existingLobby(id, owner);

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        UpdateLobbySettingsModel model = new UpdateLobbySettingsModel(5, new TicTacToeSettingsModel(5));

        Lobby updated = service.updateSettings(id, owner, model);

        assertThat(updated.settings().maxPlayers()).isEqualTo(5);
        assertThat(updated.settings().gameSettings()).isInstanceOf(TicTacToeSettings.class);
        assertThat(((TicTacToeSettings) updated.settings().gameSettings()).boardSize()).isEqualTo(5);

        verify(repo).save(lobby);
    }

    @Test
    void updateSettings_checkers_updatesSuccessfully() {

        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());
        Lobby lobby = existingLobby(id, owner);

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        UpdateLobbySettingsModel model = new UpdateLobbySettingsModel(6, new CheckersSettingsModel(8, true));

        Lobby updated = service.updateSettings(id, owner, model);

        assertThat(updated.settings().maxPlayers()).isEqualTo(6);
        assertThat(updated.settings().gameSettings()).isInstanceOf(CheckersSettings.class);

        verify(repo).save(lobby);
    }

    @Test
    void updateSettings_nullSettings_throws() {

        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());
        Lobby lobby = existingLobby(id, owner);

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        UpdateLobbySettingsModel model = new UpdateLobbySettingsModel(4, null);

        assertThatThrownBy(() -> service.updateSettings(id, owner, model)).isInstanceOf(NullPointerException.class);
    }
}

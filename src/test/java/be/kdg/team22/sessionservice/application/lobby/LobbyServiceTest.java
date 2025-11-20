package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.CheckersSettingsModel;
import be.kdg.team22.sessionservice.api.lobby.models.TicTacToeSettingsModel;
import be.kdg.team22.sessionservice.api.lobby.models.UpdateLobbySettingsModel;
import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.GameNotValidException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbySettingsInvalidException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.OwnerNotValidException;
import be.kdg.team22.sessionservice.domain.lobby.settings.CheckersSettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class LobbyServiceTest {

    LobbyRepository repo = mock(LobbyRepository.class);
    LobbyService service = new LobbyService(repo);

    @Test
    void createLobby_savesLobby_andReturnsIt() {
        GameId gameId = new GameId(UUID.randomUUID());
        PlayerId owner = new PlayerId(UUID.randomUUID());
        var model = new be.kdg.team22.sessionservice.api.lobby.models.CreateLobbyModel(
                gameId.value(),
                4,
                3
        );

        doNothing().when(repo).save(any(Lobby.class));

        Lobby result = service.createLobby(gameId, owner, model);

        assertThat(result.gameId()).isEqualTo(gameId);
        assertThat(result.ownerId()).isEqualTo(owner);
        assertThat(result.settings().maxPlayers()).isEqualTo(4);
        assertThat(result.settings().gameSettings()).isInstanceOf(TicTacToeSettings.class);
        verify(repo).save(any(Lobby.class));
    }

    @Test
    void createLobby_nullGameId_throws() {
        PlayerId owner = new PlayerId(UUID.randomUUID());
        var model = new be.kdg.team22.sessionservice.api.lobby.models.CreateLobbyModel(
                null,
                4,
                3
        );

        assertThatThrownBy(() -> service.createLobby(null, owner, model))
                .isInstanceOf(GameNotValidException.class);
    }

    @Test
    void createLobby_nullOwnerId_throws() {
        GameId gameId = new GameId(UUID.randomUUID());
        var model = new be.kdg.team22.sessionservice.api.lobby.models.CreateLobbyModel(
                gameId.value(),
                4,
                3
        );

        assertThatThrownBy(() -> service.createLobby(gameId, null, model))
                .isInstanceOf(OwnerNotValidException.class);
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

        assertThatThrownBy(() -> service.findLobby(id))
                .isInstanceOf(LobbyNotFoundException.class);
    }

    @Test
    void findAllLobbies_returnsList() {
        List<Lobby> list = List.of(mock(Lobby.class));

        when(repo.findAll()).thenReturn(list);

        assertThat(service.findAllLobbies()).containsExactlyElementsOf(list);
    }

    @Test
    void updateSettings_ticTacToe_updatesSettings() {
        LobbyId id = LobbyId.create();
        GameId gameId = new GameId(UUID.randomUUID());
        PlayerId owner = new PlayerId(UUID.randomUUID());

        LobbySettings existing = new LobbySettings(new TicTacToeSettings(3), 4);

        Lobby lobby = new Lobby(
                id,
                gameId,
                owner,
                Set.of(owner),
                existing,
                LobbyStatus.OPEN,
                java.time.Instant.now(),
                java.time.Instant.now()
        );

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        UpdateLobbySettingsModel model = new UpdateLobbySettingsModel(
                5,
                new TicTacToeSettingsModel(5)
        );

        Lobby result = service.updateSettings(id, owner, model);

        assertThat(result.settings().maxPlayers()).isEqualTo(5);
        assertThat(result.settings().gameSettings()).isInstanceOf(TicTacToeSettings.class);
        assertThat(((TicTacToeSettings) result.settings().gameSettings()).boardSize())
                .isEqualTo(5);
        verify(repo).save(lobby);
    }

    @Test
    void updateSettings_checkers_updatesSettings() {
        LobbyId id = LobbyId.create();
        GameId gameId = new GameId(UUID.randomUUID());
        PlayerId owner = new PlayerId(UUID.randomUUID());

        LobbySettings existing = new LobbySettings(new TicTacToeSettings(3), 4);

        Lobby lobby = new Lobby(
                id,
                gameId,
                owner,
                Set.of(owner),
                existing,
                LobbyStatus.OPEN,
                java.time.Instant.now(),
                java.time.Instant.now()
        );

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        UpdateLobbySettingsModel model = new UpdateLobbySettingsModel(
                6,
                new CheckersSettingsModel(8, true)
        );

        Lobby result = service.updateSettings(id, owner, model);

        assertThat(result.settings().maxPlayers()).isEqualTo(6);
        assertThat(result.settings().gameSettings()).isInstanceOf(CheckersSettings.class);
        verify(repo).save(lobby);
    }

    @Test
    void updateSettings_nullSettings_throwsIllegalArgument() {
        LobbyId id = LobbyId.create();
        GameId gameId = new GameId(UUID.randomUUID());
        PlayerId owner = new PlayerId(UUID.randomUUID());

        LobbySettings existing = new LobbySettings(new TicTacToeSettings(3), 4);

        Lobby lobby = new Lobby(
                id,
                gameId,
                owner,
                Set.of(owner),
                existing,
                LobbyStatus.OPEN,
                java.time.Instant.now(),
                java.time.Instant.now()
        );

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        UpdateLobbySettingsModel model = new UpdateLobbySettingsModel(4, null);

        assertThatThrownBy(() -> service.updateSettings(id, owner, model))
                .isInstanceOf(LobbySettingsInvalidException.class);
    }
}

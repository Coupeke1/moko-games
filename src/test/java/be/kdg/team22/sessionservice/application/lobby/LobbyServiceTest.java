package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.GameNotValidException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyCreationException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.OwnerNotValidException;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
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

        doNothing().when(repo).save(any(Lobby.class));

        Lobby result = service.createLobby(gameId, owner);

        assertThat(result.gameId()).isEqualTo(gameId);
        assertThat(result.ownerId()).isEqualTo(owner);
        verify(repo).save(any(Lobby.class));
    }

    @Test
    void createLobby_nullGameId_throws() {
        PlayerId owner = new PlayerId(UUID.randomUUID());
        assertThatThrownBy(() -> service.createLobby(null, owner))
                .isInstanceOf(GameNotValidException.class);
    }

    @Test
    void createLobby_nullOwnerId_throws() {
        GameId gameId = new GameId(UUID.randomUUID());
        assertThatThrownBy(() -> service.createLobby(gameId, null))
                .isInstanceOf(OwnerNotValidException.class);
    }

    @Test
    void createLobby_rethrowsAsLobbyCreationException() {
        GameId gameId = new GameId(UUID.randomUUID());
        PlayerId owner = new PlayerId(UUID.randomUUID());

        doThrow(new RuntimeException("DB error")).when(repo).save(any());

        assertThatThrownBy(() -> service.createLobby(gameId, owner))
                .isInstanceOf(LobbyCreationException.class)
                .hasMessageContaining("DB error");
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

    // ---------- NIEUWE TESTS VOOR CLOSE & SETTINGS ----------

    @Test
    void closeLobby_closesAndSaves() {
        GameId gameId = new GameId(UUID.randomUUID());
        PlayerId owner = new PlayerId(UUID.randomUUID());
        Lobby lobby = new Lobby(gameId, owner);
        LobbyId id = lobby.id();

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        Lobby result = service.closeLobby(id, owner);

        assertThat(result.status()).isEqualTo(LobbyStatus.CANCELLED);
        verify(repo).save(lobby);
    }

    @Test
    void closeLobby_notFound_throws() {
        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());

        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.closeLobby(id, owner))
                .isInstanceOf(LobbyNotFoundException.class);
    }

    @Test
    void updateSettings_updatesAndSaves() {
        GameId gameId = new GameId(UUID.randomUUID());
        PlayerId owner = new PlayerId(UUID.randomUUID());
        Lobby lobby = new Lobby(gameId, owner);
        LobbyId id = lobby.id();

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        LobbySettings newSettings =
                new LobbySettings(new TicTacToeSettings(3), 5);

        Lobby result = service.updateSettings(id, owner, newSettings);

        assertThat(result.settings().maxPlayers()).isEqualTo(5);
        assertThat(result.settings().gameSettings()).isInstanceOf(TicTacToeSettings.class);
        verify(repo).save(lobby);
    }

    @Test
    void updateSettings_notFound_throws() {
        LobbyId id = LobbyId.create();
        PlayerId owner = new PlayerId(UUID.randomUUID());
        LobbySettings settings =
                new LobbySettings(new TicTacToeSettings(3), 4);

        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateSettings(id, owner, settings))
                .isInstanceOf(LobbyNotFoundException.class);
    }
}

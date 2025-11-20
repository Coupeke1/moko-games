package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.GameNotValidException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyCreationException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.OwnerNotValidException;
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

        Lobby lobby = new Lobby(gameId, owner);

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
        LobbyId id = LobbyId.newId();
        Lobby lobby = mock(Lobby.class);

        when(repo.findById(id)).thenReturn(Optional.of(lobby));

        assertThat(service.findLobby(id)).isEqualTo(lobby);
    }

    @Test
    void findLobby_notFound_throws() {
        LobbyId id = LobbyId.newId();

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
}

package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.LobbyJpaRepository;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.entities.LobbyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static be.kdg.team22.sessionservice.utils.Token.tokenWithUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LobbyControllerTest {

    @Autowired
    private MockMvc mock;

    @Autowired
    private LobbyJpaRepository repo;

    @BeforeEach
    void setup() {
        repo.deleteAll();
    }

    private LobbyEntity lobbyEntity(
            UUID id,
            UUID gameId,
            UUID ownerId,
            Set<UUID> players,
            LobbySettings settings
    ) {
        return new LobbyEntity(
                id,
                gameId,
                ownerId,
                players,
                settings,
                LobbyStatus.OPEN,
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void createLobby_createsRecord_andReturns201() throws Exception {
        String owner = "11111111-1111-1111-1111-111111111111";

        mock.perform(post("/api/lobbies")
                        .with(tokenWithUser(owner, "user", "user@kdg.be"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "gameId": "00000000-0000-0000-0000-000000000005",
                                  "maxPlayers": 4,
                                  "boardSize": 3
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId").value(owner))
                .andExpect(jsonPath("$.gameId").value("00000000-0000-0000-0000-000000000005"))
                .andExpect(jsonPath("$.players[0]").value(owner))
                .andExpect(jsonPath("$.maxPlayers").value(4))
                .andExpect(jsonPath("$.settings.type").value("ticTacToe"))
                .andExpect(jsonPath("$.settings.boardSize").value(3));

        assertThat(repo.count()).isEqualTo(1);
    }

    @Test
    void createLobby_missingJwt_returns401() throws Exception {
        mock.perform(post("/api/lobbies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "gameId": "00000000-0000-0000-0000-000000000005",
                                  "maxPlayers": 4,
                                  "boardSize": 3
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createLobby_invalidGameId_returns400() throws Exception {
        String owner = "22222222-2222-2222-2222-222222222222";

        mock.perform(post("/api/lobbies")
                        .with(tokenWithUser(owner, "user", "user@kdg.be"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "gameId": null,
                                  "maxPlayers": 4,
                                  "boardSize": 3
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLobbyById_returnsCorrectLobby() throws Exception {
        UUID id = UUID.randomUUID();
        UUID gameId = UUID.fromString("00000000-0000-0000-0000-000000000005");
        UUID ownerId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);

        repo.save(lobbyEntity(
                id, gameId, ownerId, Set.of(ownerId), settings
        ));

        mock.perform(get("/api/lobbies/" + id)
                        .with(tokenWithUser(ownerId.toString(), "owner", "owner@kdg.be")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.gameId").value(gameId.toString()))
                .andExpect(jsonPath("$.ownerId").value(ownerId.toString()))
                .andExpect(jsonPath("$.maxPlayers").value(4))
                .andExpect(jsonPath("$.settings.type").value("ticTacToe"))
                .andExpect(jsonPath("$.settings.boardSize").value(3));
    }

    @Test
    void getLobbyById_returns404IfNotFound() throws Exception {
        mock.perform(get("/api/lobbies/ffffffff-ffff-ffff-ffff-ffffffffffff")
                        .with(tokenWithUser("33333333-3333-3333-3333-333333333333", "user", "user@kdg.be")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_returns200_andList() throws Exception {
        UUID ownerId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);

        repo.save(lobbyEntity(
                UUID.randomUUID(), UUID.randomUUID(), ownerId, Set.of(ownerId), settings
        ));

        mock.perform(get("/api/lobbies")
                        .with(tokenWithUser(ownerId.toString(), "user", "user@kdg.be")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void closeLobby_owner_succeeds() throws Exception {
        UUID id = UUID.randomUUID();
        UUID ownerId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);

        repo.save(lobbyEntity(
                id, UUID.randomUUID(), ownerId, Set.of(ownerId), settings
        ));

        mock.perform(post("/api/lobbies/" + id + "/close")
                        .with(tokenWithUser(ownerId.toString(), "owner", "owner@kdg.be"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    void closeLobby_nonOwner_returnsBadRequest() throws Exception {
        UUID id = UUID.randomUUID();
        UUID ownerId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID otherId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);

        repo.save(lobbyEntity(
                id, UUID.randomUUID(), ownerId, Set.of(ownerId), settings
        ));

        mock.perform(post("/api/lobbies/" + id + "/close")
                        .with(tokenWithUser(otherId.toString(), "other", "other@kdg.be"))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSettings_owner_succeeds() throws Exception {
        UUID id = UUID.randomUUID();
        UUID ownerId = UUID.fromString("12121212-1212-1212-1212-121212121212");

        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);

        repo.save(lobbyEntity(
                id, UUID.randomUUID(), ownerId, Set.of(ownerId), settings
        ));

        mock.perform(put("/api/lobbies/" + id + "/settings")
                        .with(tokenWithUser(ownerId.toString(), "owner", "owner@kdg.be"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "maxPlayers": 5,
                                  "settings": {
                                    "type": "ticTacToe",
                                    "boardSize": 4
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxPlayers").value(5))
                .andExpect(jsonPath("$.settings.boardSize").value(4));
    }

    @Test
    void updateSettings_nonOwner_returnsBadRequest() throws Exception {
        UUID id = UUID.randomUUID();
        UUID ownerId = UUID.fromString("13131313-1313-1313-1313-131313131313");
        UUID otherId = UUID.fromString("14141414-1414-1414-1414-141414141414");

        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);

        repo.save(lobbyEntity(
                id, UUID.randomUUID(), ownerId, Set.of(ownerId), settings
        ));

        mock.perform(put("/api/lobbies/" + id + "/settings")
                        .with(tokenWithUser(otherId.toString(), "other", "other@kdg.be"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "maxPlayers": 6,
                                  "settings": {
                                    "type": "ticTacToe",
                                    "boardSize": 3
                                  }
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}

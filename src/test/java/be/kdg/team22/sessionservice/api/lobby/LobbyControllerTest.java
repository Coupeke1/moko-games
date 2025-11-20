package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    void createLobby_createsRecord_andReturns201() throws Exception {
        String owner = "11111111-1111-1111-1111-111111111111";

        mock.perform(post("/api/lobbies")
                        .with(tokenWithUser(owner, "user", "user@kdg.be"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "gameId": "00000000-0000-0000-0000-000000000005" }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId").value(owner))
                .andExpect(jsonPath("$.gameId").value("00000000-0000-0000-0000-000000000005"))
                .andExpect(jsonPath("$.players[0]").value(owner));

        assertThat(repo.count()).isEqualTo(1);
    }

    @Test
    void createLobby_missingJwt_returns401() throws Exception {
        mock.perform(post("/api/lobbies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "gameId": "00000000-0000-0000-0000-000000000005" }
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
                                { "gameId": null }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLobbyById_returnsCorrectLobby() throws Exception {
        UUID id = UUID.randomUUID();
        UUID gameId = UUID.fromString("00000000-0000-0000-0000-000000000005");
        UUID ownerId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        repo.save(new LobbyEntity(
                id,
                gameId,
                ownerId,
                Set.of(ownerId),
                LobbyStatus.OPEN,
                Instant.now(),
                Instant.now()
        ));

        mock.perform(get("/api/lobbies/" + id)
                        .with(tokenWithUser(ownerId.toString(), "owner", "owner@kdg.be")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.gameId").value(gameId.toString()))
                .andExpect(jsonPath("$.ownerId").value(ownerId.toString()));
    }

    @Test
    void getLobbyById_returns404IfNotFound() throws Exception {
        String someUser = "33333333-3333-3333-3333-333333333333";

        mock.perform(get("/api/lobbies/ffffffff-ffff-ffff-ffff-ffffffffffff")
                        .with(tokenWithUser(someUser, "user", "user@kdg.be")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_returns200_andList() throws Exception {
        UUID ownerId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

        repo.save(new LobbyEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                ownerId,
                Set.of(ownerId),
                LobbyStatus.OPEN,
                Instant.now(),
                Instant.now()
        ));

        mock.perform(get("/api/lobbies")
                        .with(tokenWithUser(ownerId.toString(), "user", "user@kdg.be")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }
}

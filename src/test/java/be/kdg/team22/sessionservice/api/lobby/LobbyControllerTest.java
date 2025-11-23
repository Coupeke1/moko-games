package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.application.lobby.LobbyInviteQueryService;
import be.kdg.team22.sessionservice.application.lobby.LobbyPlayerService;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.application.player.PlayerService;
import be.kdg.team22.sessionservice.config.TestSecurityConfig;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.GameNotValidException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.PlayerName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LobbyController.class)
@Import(TestSecurityConfig.class)
class LobbyControllerTest {
    private static final UUID GAME_ID = UUID.fromString("00000000-0000-0000-0000-000000000005");

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private LobbyService lobbyService;
    @MockitoBean
    private LobbyPlayerService lobbyPlayerService;
    @MockitoBean
    private PlayerService playerService;
    @MockitoBean
    private LobbyInviteQueryService inviteQueryService;
    @MockitoBean
    private JwtDecoder jwtDecoder;

    private static RequestPostProcessor jwtFor(String sub, String username, String email) {
        return jwt().jwt(builder -> {
            builder.subject(sub);
            builder.claim("preferred_username", username);
            builder.claim("email", email);
        });
    }

    private Lobby sampleLobby(UUID lobbyId, UUID ownerId) {
        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);
        Player ownerPlayer = new Player(PlayerId.from(ownerId), new PlayerName("owner"));

        return new Lobby(LobbyId.from(lobbyId), GameId.from(GAME_ID), PlayerId.from(ownerId), List.of(ownerPlayer), Set.of(), settings, LobbyStatus.OPEN, Instant.parse("2024-01-01T00:00:00Z"), Instant.parse("2024-01-01T00:00:00Z"));
    }

    @Test
    @DisplayName("POST /api/lobbies – creates lobby and returns 201")
    void createLobby_createsRecord_andReturns201() throws Exception {
        String ownerId = "11111111-1111-1111-1111-111111111111";
        UUID lobbyId = UUID.randomUUID();

        Lobby lobby = sampleLobby(lobbyId, UUID.fromString(ownerId));

        when(lobbyService.createLobby(any(GameId.class), any(PlayerId.class), any(), any(Jwt.class)))
                .thenReturn(lobby);

        mockMvc.perform(post("/api/lobbies")
                        .with(jwtFor(ownerId, "user", "user@kdg.be"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "gameId": "00000000-0000-0000-0000-000000000005",
                                  "maxPlayers": 4,
                                  "settings": {
                                    "type": "ticTacToe",
                                    "boardSize": 3
                                  }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.gameId").value(GAME_ID.toString()))
                .andExpect(jsonPath("$.maxPlayers").value(4))
                .andExpect(jsonPath("$.settings.type").value("ticTacToe"))
                .andExpect(jsonPath("$.settings.boardSize").value(3));

        ArgumentCaptor<GameId> gameIdCaptor = ArgumentCaptor.forClass(GameId.class);
        ArgumentCaptor<PlayerId> ownerCaptor = ArgumentCaptor.forClass(PlayerId.class);

        verify(lobbyService).createLobby(
                gameIdCaptor.capture(),
                ownerCaptor.capture(),
                any(),
                any(Jwt.class)
        );

        assertThat(gameIdCaptor.getValue().value()).isEqualTo(GAME_ID);
        assertThat(ownerCaptor.getValue().value().toString()).isEqualTo(ownerId);
    }


    @Test
    @DisplayName("POST /api/lobbies – missing JWT returns 401")
    void createLobby_missingJwt_returns401() throws Exception {
        mockMvc.perform(post("/api/lobbies").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "gameId": "00000000-0000-0000-0000-000000000005",
                  "maxPlayers": 4,
                  "settings": {
                    "type": "ticTacToe",
                    "boardSize": 3
                  }
                }
                """)).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/lobbies – invalid gameId returns 400 (GameNotValidException)")
    void createLobby_invalidGameId_returns400() throws Exception {
        String ownerId = "22222222-2222-2222-2222-222222222222";

        when(lobbyService.createLobby(any(), any(), any(), any(Jwt.class))).thenThrow(new GameNotValidException(null));

        mockMvc.perform(post("/api/lobbies").with(jwtFor(ownerId, "user", "user@kdg.be")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "gameId": null,
                  "maxPlayers": 4,
                  "settings": {
                    "type": "ticTacToe",
                    "boardSize": 3
                  }
                }
                """)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/lobbies/{id} – returns lobby when found")
    void getLobbyById_returnsCorrectLobby() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        Lobby lobby = sampleLobby(lobbyId, ownerId);

        when(lobbyService.findLobby(LobbyId.from(lobbyId))).thenReturn(lobby);
        mockMvc.perform(get("/api/lobbies/{id}", lobbyId).with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be"))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(lobbyId.toString())).andExpect(jsonPath("$.gameId").value(GAME_ID.toString())).andExpect(jsonPath("$.ownerId").value(ownerId.toString())).andExpect(jsonPath("$.maxPlayers").value(4)).andExpect(jsonPath("$.settings.type").value("ticTacToe")).andExpect(jsonPath("$.settings.boardSize").value(3));
    }

    @Test
    @DisplayName("GET /api/lobbies/{id} – returns 404 when not found")
    void getLobbyById_returns404IfNotFound() throws Exception {
        UUID rawId = UUID.randomUUID();
        LobbyId lobbyId = LobbyId.from(rawId);

        when(lobbyService.findLobby(lobbyId)).thenThrow(new LobbyNotFoundException(lobbyId));

        mockMvc.perform(get("/api/lobbies/{id}", rawId).with(jwtFor("33333333-3333-3333-3333-333333333333", "user", "user@kdg.be"))).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/lobbies – returns list of lobbies")
    void getAll_returns200_andList() throws Exception {
        UUID ownerId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        Lobby lobby = sampleLobby(UUID.randomUUID(), ownerId);

        when(lobbyService.findAllLobbies()).thenReturn(List.of(lobby));
        mockMvc.perform(get("/api/lobbies").with(jwtFor(ownerId.toString(), "user", "user@kdg.be"))).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(lobby.id().value().toString()));
    }

    @Test
    @DisplayName("POST /api/lobbies/{id}/close – owner can close lobby")
    void closeLobby_owner_succeeds() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        Lobby lobby = sampleLobby(lobbyId, ownerId);

        Lobby closedLobby = new Lobby(lobby.id(), lobby.gameId(), lobby.ownerId(), List.copyOf(lobby.players()), lobby.invitedPlayers(), lobby.settings(), LobbyStatus.CLOSED, lobby.createdAt(), Instant.parse("2024-01-02T00:00:00Z"));

        when(lobbyService.closeLobby(LobbyId.from(lobbyId), PlayerId.from(ownerId))).thenReturn(closedLobby);
        mockMvc.perform(post("/api/lobbies/{id}/close", lobbyId).with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be")).with(csrf())).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    @DisplayName("PUT /api/lobbies/{id}/settings – owner can update settings")
    void updateSettings_owner_succeeds() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.fromString("12121212-1212-1212-1212-121212121212");

        Lobby lobby = sampleLobby(lobbyId, ownerId);

        LobbySettings newSettings = new LobbySettings(new TicTacToeSettings(4), 5);
        Lobby updated = new Lobby(lobby.id(), lobby.gameId(), lobby.ownerId(), List.copyOf(lobby.players()), lobby.invitedPlayers(), newSettings, lobby.status(), lobby.createdAt(), Instant.parse("2024-01-03T00:00:00Z"));

        when(lobbyService.updateSettings(eq(LobbyId.from(lobbyId)), eq(PlayerId.from(ownerId)), any())).thenReturn(updated);

        mockMvc.perform(put("/api/lobbies/{id}/settings", lobbyId).with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "maxPlayers": 5,
                  "settings": {
                    "type": "ticTacToe",
                    "boardSize": 4
                  }
                }
                """)).andExpect(status().isOk()).andExpect(jsonPath("$.maxPlayers").value(5)).andExpect(jsonPath("$.settings.boardSize").value(4));
    }

    @Test
    @DisplayName("POST /api/lobbies/{lobbyId}/invite/{playerId} – calls lobbyPlayerService.invitePlayer")
    void inviteSinglePlayer_callsService() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000000");
        UUID targetId = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000000");

        doNothing().when(lobbyPlayerService).invitePlayer(any(PlayerId.class), any(LobbyId.class), any(PlayerId.class), any(Jwt.class));

        mockMvc.perform(post("/api/lobbies/{lobbyId}/invite/{playerId}", lobbyId, targetId).with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be")).with(csrf())).andExpect(status().isOk());

        verify(lobbyPlayerService).invitePlayer(eq(PlayerId.from(ownerId)), eq(LobbyId.from(lobbyId)), eq(PlayerId.from(targetId)), any(Jwt.class));
    }

    @Test
    @DisplayName("POST /api/lobbies/{lobbyId}/invite – calls lobbyPlayerService.invitePlayers")
    void inviteMultiplePlayers_callsService() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.fromString("cccccccc-0000-0000-0000-000000000000");
        UUID p1 = UUID.fromString("dddddddd-0000-0000-0000-000000000000");
        UUID p2 = UUID.fromString("eeeeeeee-0000-0000-0000-000000000000");

        doNothing().when(lobbyPlayerService).invitePlayers(any(PlayerId.class), any(LobbyId.class), anyList(), any(Jwt.class));

        String body = """
                {
                  "playerIds": [
                    "%s",
                    "%s"
                  ]
                }
                """.formatted(p1, p2);

        mockMvc.perform(post("/api/lobbies/{lobbyId}/invite", lobbyId).with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());

        ArgumentCaptor<List<PlayerId>> idsCaptor = ArgumentCaptor.forClass(List.class);

        verify(lobbyPlayerService).invitePlayers(eq(PlayerId.from(ownerId)), eq(LobbyId.from(lobbyId)), idsCaptor.capture(), any(Jwt.class));

        List<UUID> capturedIds = idsCaptor.getValue().stream().map(PlayerId::value).collect(Collectors.toList());

        assertThat(capturedIds).containsExactlyInAnyOrder(p1, p2);
    }

    @Test
    @DisplayName("POST /api/lobbies/{lobbyId}/players/{playerId} – acceptInvite happy path")
    void acceptInvite_succeeds() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID playerId = UUID.fromString("99999999-0000-0000-0000-000000000000");

        doNothing().when(lobbyPlayerService).acceptInvite(eq(PlayerId.from(playerId)), eq(LobbyId.from(lobbyId)), any(Jwt.class));

        mockMvc.perform(post("/api/lobbies/{lobbyId}/players/{playerId}", lobbyId, playerId).with(jwtFor(playerId.toString(), "kaj", "kaj@kdg.be")).with(csrf())).andExpect(status().isOk());

        verify(lobbyPlayerService).acceptInvite(eq(PlayerId.from(playerId)), eq(LobbyId.from(lobbyId)), any(Jwt.class));
    }

    @Test
    @DisplayName("POST /api/lobbies/{lobbyId}/players/{playerId} – id mismatch returns 404")
    void acceptInvite_idMismatch_returnsBadRequest() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID pathPlayerId = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000000");
        UUID tokenSub = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000000");

        mockMvc.perform(post("/api/lobbies/{lobbyId}/players/{playerId}", lobbyId, pathPlayerId).with(jwtFor(tokenSub.toString(), "other", "other@kdg.be")).with(csrf())).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/lobbies/{lobbyId}/players/{playerId} – calls lobbyPlayerService.removePlayer")
    void removePlayer_callsService() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.fromString("12121212-0000-0000-0000-000000000000");
        UUID targetId = UUID.fromString("13131313-0000-0000-0000-000000000000");

        doNothing().when(lobbyPlayerService).removePlayer(any(PlayerId.class), any(LobbyId.class), any(PlayerId.class));

        mockMvc.perform(delete("/api/lobbies/{lobbyId}/players/{playerId}", lobbyId, targetId).with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be")).with(csrf())).andExpect(status().isOk());

        verify(lobbyPlayerService).removePlayer(eq(PlayerId.from(ownerId)), eq(LobbyId.from(lobbyId)), eq(PlayerId.from(targetId)));
    }

    @Test
    @DisplayName("DELETE /api/lobbies/{lobbyId}/players – calls lobbyPlayerService.removePlayers")
    void removePlayers_callsService() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.fromString("14141414-0000-0000-0000-000000000000");
        UUID t1 = UUID.fromString("15151515-0000-0000-0000-000000000000");
        UUID t2 = UUID.fromString("16161616-0000-0000-0000-000000000000");

        doNothing().when(lobbyPlayerService).removePlayers(any(PlayerId.class), any(LobbyId.class), anyList());

        String body = """
                {
                  "playerIds": [
                    "%s",
                    "%s"
                  ]
                }
                """.formatted(t1, t2);

        mockMvc.perform(delete("/api/lobbies/{lobbyId}/players", lobbyId).with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());

        verify(lobbyPlayerService).removePlayers(eq(PlayerId.from(ownerId)), eq(LobbyId.from(lobbyId)), argThat(list -> list.stream().map(PlayerId::value).collect(Collectors.toSet()).containsAll(Set.of(t1, t2))));
    }
}

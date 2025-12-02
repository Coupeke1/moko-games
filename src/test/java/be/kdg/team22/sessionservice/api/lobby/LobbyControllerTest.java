package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.application.lobby.InviteQueryService;
import be.kdg.team22.sessionservice.application.lobby.LobbyPlayerService;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.application.player.PlayerService;
import be.kdg.team22.sessionservice.config.TestSecurityConfig;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.*;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    private InviteQueryService inviteQueryService;
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

        Player ownerPlayer = new Player(PlayerId.from(ownerId), new PlayerName("owner"), "");
        Player player = new Player(PlayerId.from(ownerId), new PlayerName("player"), "");

        ownerPlayer.setReady();
        player.setReady();

        Lobby lobby = new Lobby(
                LobbyId.from(lobbyId),
                GameId.from(GAME_ID),
                PlayerId.from(ownerId),
                List.of(ownerPlayer, player),
                Set.of(),
                settings,
                LobbyStatus.OPEN,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:00Z")
        );

        lobby.markStarted(GameId.from(UUID.randomUUID()));

        return lobby;
    }

    @Test
    @DisplayName("POST /api/lobbies – creates lobby and returns 201")
    void createLobby_createsRecord_andReturns201() throws Exception {
        String ownerId = "11111111-1111-1111-1111-111111111111";
        UUID lobbyId = UUID.randomUUID();

        Lobby lobby = sampleLobby(lobbyId, UUID.fromString(ownerId));

        when(lobbyService.createLobby(any(GameId.class), any(PlayerId.class), any(), any(Jwt.class))).thenReturn(lobby);

        mockMvc.perform(post("/api/lobbies").with(jwtFor(ownerId, "user", "user@kdg.be")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "gameId": "00000000-0000-0000-0000-000000000005",
                  "maxPlayers": 4,
                  "settings": {
                    "type": "ticTacToe",
                    "boardSize": 3
                  }
                }
                """)).andExpect(status().isCreated()).andExpect(jsonPath("$.ownerId").value(ownerId)).andExpect(jsonPath("$.gameId").value(GAME_ID.toString())).andExpect(jsonPath("$.maxPlayers").value(4)).andExpect(jsonPath("$.settings.type").value("ticTacToe")).andExpect(jsonPath("$.settings.boardSize").value(3));

        ArgumentCaptor<GameId> gameIdCaptor = ArgumentCaptor.forClass(GameId.class);
        ArgumentCaptor<PlayerId> ownerCaptor = ArgumentCaptor.forClass(PlayerId.class);

        verify(lobbyService).createLobby(gameIdCaptor.capture(), ownerCaptor.capture(), any(), any(Jwt.class));

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
    @DisplayName("POST /api/lobbies/{lobbyId}/start – owner starts lobby successfully")
    void startLobby_callsService() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.fromString("88888888-0000-0000-0000-000000000000");

        Lobby lobby = sampleLobby(lobbyId, ownerId);

        when(lobbyService.startLobby(eq(LobbyId.from(lobbyId)), eq(PlayerId.from(ownerId)), any(Jwt.class))).thenReturn(lobby);

        mockMvc.perform(post("/api/lobbies/{lobbyId}/start", lobbyId).with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be")).with(csrf())).andExpect(status().isOk());

        verify(lobbyService).startLobby(eq(LobbyId.from(lobbyId)), eq(PlayerId.from(ownerId)), any(Jwt.class));
    }

    @Test
    @DisplayName("POST /api/lobbies/{lobbyId}/start – fails when players not ready")
    void startLobby_playersNotReady_returns400() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.fromString("20202020-0000-0000-0000-000000000000");

        when(lobbyService.startLobby(eq(LobbyId.from(lobbyId)), eq(PlayerId.from(ownerId)), any(Jwt.class))).thenThrow(new PlayersNotReadyException(lobbyId));

        mockMvc.perform(post("/api/lobbies/{lobbyId}/start", lobbyId).with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be")).with(csrf())).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/lobbies/{lobbyId}/start – fails when non-owner tries to start")
    void startLobby_notOwner_returns400() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID otherUserId = UUID.fromString("30303030-0000-0000-0000-000000000000");

        when(lobbyService.startLobby(eq(LobbyId.from(lobbyId)), eq(PlayerId.from(otherUserId)), any(Jwt.class))).thenThrow(new NotLobbyOwnerException(new PlayerId(otherUserId)));

        mockMvc.perform(post("/api/lobbies/{lobbyId}/start", lobbyId).with(jwtFor(otherUserId.toString(), "other", "other@kdg.be")).with(csrf())).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/lobbies/{lobbyId}/start – game service unreachable → 503")
    void startLobby_gameServiceUnreachable_returns503() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.fromString("40404040-0000-0000-0000-000000000000");

        when(lobbyService.startLobby(eq(LobbyId.from(lobbyId)), eq(PlayerId.from(ownerId)), any(Jwt.class))).thenThrow(new NotReachableException());

        mockMvc.perform(post("/api/lobbies/{lobbyId}/start", lobbyId).with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be")).with(csrf())).andExpect(status().isServiceUnavailable());
    }
}
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
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
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
import java.util.Map;
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
            builder.header("alg", "none");
            builder.subject(sub);
            builder.claim("preferred_username", username);
            builder.claim("email", email);
        });
    }

    private static LobbySettings lobbySettings(int maxPlayers, int boardSize) {
        return new LobbySettings(maxPlayers, Map.of("boardSize", boardSize));
    }

    private Lobby sampleLobby(UUID lobbyId, UUID ownerId, int maxPlayers, int boardSize) {
        Player ownerPlayer = new Player(PlayerId.from(ownerId), new PlayerName("owner"), "");
        Player otherPlayer = new Player(PlayerId.from(UUID.randomUUID()), new PlayerName("player"), "");

        ownerPlayer.setReady();
        otherPlayer.setReady();

        return new Lobby(
                LobbyId.from(lobbyId),
                GameId.from(GAME_ID),
                PlayerId.from(ownerId),
                List.of(ownerPlayer, otherPlayer),
                Set.of(),
                lobbySettings(maxPlayers, boardSize),
                LobbyStatus.OPEN,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:00Z")
        );
    }

    @Test
    @DisplayName("POST /api/lobbies – creates lobby and returns 201")
    void createLobby_createsRecord_andReturns201() throws Exception {
        String ownerId = "11111111-1111-1111-1111-111111111111";
        UUID lobbyId = UUID.randomUUID();

        Lobby lobby = sampleLobby(lobbyId, UUID.fromString(ownerId), 4, 3);

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
                                    "boardSize": 3
                                  }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.gameId").value(GAME_ID.toString()))
                .andExpect(jsonPath("$.maxPlayers").value(4))
                .andExpect(jsonPath("$.settings.boardSize").value(3));

        ArgumentCaptor<GameId> gameIdCaptor = ArgumentCaptor.forClass(GameId.class);
        ArgumentCaptor<PlayerId> ownerCaptor = ArgumentCaptor.forClass(PlayerId.class);

        verify(lobbyService).createLobby(gameIdCaptor.capture(), ownerCaptor.capture(), any(), any(Jwt.class));

        assertThat(gameIdCaptor.getValue().value()).isEqualTo(GAME_ID);
        assertThat(ownerCaptor.getValue().value().toString()).isEqualTo(ownerId);
    }

    @Test
    @DisplayName("GET /api/lobbies/{id} – returns lobby when found")
    void getLobbyById_returnsCorrectLobby() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        Lobby lobby = sampleLobby(lobbyId, ownerId, 4, 3);

        when(lobbyService.findLobby(LobbyId.from(lobbyId))).thenReturn(lobby);

        mockMvc.perform(get("/api/lobbies/{id}", lobbyId)
                        .with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lobbyId.toString()))
                .andExpect(jsonPath("$.gameId").value(GAME_ID.toString()))
                .andExpect(jsonPath("$.ownerId").value(ownerId.toString()))
                .andExpect(jsonPath("$.maxPlayers").value(4))
                .andExpect(jsonPath("$.settings.boardSize").value(3));
    }

    @Test
    @DisplayName("PUT /api/lobbies/{id}/settings – owner can update settings")
    void updateSettings_owner_succeeds() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.fromString("12121212-1212-1212-1212-121212121212");

        Lobby original = sampleLobby(lobbyId, ownerId, 4, 3);
        LobbySettings newSettings = lobbySettings(5, 4);

        Lobby updated = new Lobby(
                original.id(),
                original.gameId(),
                original.ownerId(),
                List.copyOf(original.players()),
                original.invitedPlayers(),
                newSettings,
                original.status(),
                original.createdAt(),
                Instant.parse("2024-01-03T00:00:00Z")
        );

        when(lobbyService.updateSettings(eq(LobbyId.from(lobbyId)), eq(PlayerId.from(ownerId)), any(), any(Jwt.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/lobbies/{id}/settings", lobbyId)
                        .with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "maxPlayers": 5,
                                  "settings": {
                                    "boardSize": 4
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxPlayers").value(5))
                .andExpect(jsonPath("$.settings.boardSize").value(4));

        verify(lobbyService).updateSettings(eq(LobbyId.from(lobbyId)), eq(PlayerId.from(ownerId)), any(), any(Jwt.class));
    }
}
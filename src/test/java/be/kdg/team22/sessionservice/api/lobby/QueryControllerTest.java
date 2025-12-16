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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QueryController.class)
@Import(TestSecurityConfig.class)
class QueryControllerTest {

    private static final UUID GAME_ID = UUID.fromString("00000000-0000-0000-0000-000000000005");
    private final LobbySettings settings = new LobbySettings(
            2,
            Map.of("boardSize", 3)
    );
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
        Player owner = new Player(PlayerId.from(ownerId), new PlayerName("owner"), "");

        return new Lobby(
                LobbyId.from(lobbyId),
                GameId.from(GAME_ID),
                PlayerId.from(ownerId),
                List.of(owner),
                Set.of(),
                settings,
                LobbyStatus.OPEN,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:00Z")
        );
    }

    @Test
    @DisplayName("GET /api/lobbies – returns 200 with list")
    void getAll_returnsList() throws Exception {
        UUID ownerId = UUID.randomUUID();
        Lobby lobby = sampleLobby(UUID.randomUUID(), ownerId);

        when(lobbyService.findAllLobbies()).thenReturn(List.of(lobby));

        mockMvc.perform(get("/api/lobbies")
                        .with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(lobby.id().value().toString()));
    }

    @Test
    @DisplayName("GET /api/lobbies/{lobbyId}/invited – returns invited player IDs")
    void getInvitedPlayers_returnsListOfIds() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID invitedId = UUID.randomUUID();

        Player owner = new Player(PlayerId.from(ownerId), new PlayerName("owner"), "");

        Lobby lobby = new Lobby(
                LobbyId.from(lobbyId),
                GameId.from(GAME_ID),
                PlayerId.from(ownerId),
                List.of(owner),
                Set.of(PlayerId.from(invitedId)),
                settings,
                LobbyStatus.OPEN,
                Instant.now(),
                Instant.now()
        );

        when(lobbyService.findLobby(LobbyId.from(lobbyId))).thenReturn(lobby);

        mockMvc.perform(get("/api/lobbies/{lobbyId}/invited", lobbyId)
                        .with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(invitedId.toString()));
    }

    @Test
    @DisplayName("GET /api/lobbies/invited/{gameId}/me – returns lobby invites for the user")
    void getInvitesForPlayer_returnsListOfLobbies() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID lobbyId = UUID.randomUUID();

        Lobby lobby = sampleLobby(lobbyId, userId);

        when(lobbyService.getInvitesFromPlayer(
                eq(PlayerId.from(userId)),
                eq(new GameId(GAME_ID))
        )).thenReturn(List.of(lobby));

        mockMvc.perform(get("/api/lobbies/invited/{gameId}/me", GAME_ID)
                        .with(jwtFor(userId.toString(), "user", "u@kdg.be")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(lobby.id().value().toString()));
    }
}
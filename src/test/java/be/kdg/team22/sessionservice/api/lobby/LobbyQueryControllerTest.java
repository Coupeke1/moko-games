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
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
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
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LobbyQueryController.class)
@Import(TestSecurityConfig.class)
class LobbyQueryControllerTest {
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
        Player ownerPlayer = new Player(PlayerId.from(ownerId), new PlayerName("owner"), "");

        return new Lobby(LobbyId.from(lobbyId), GameId.from(GAME_ID), PlayerId.from(ownerId), List.of(ownerPlayer), Set.of(), settings, LobbyStatus.OPEN, Instant.parse("2024-01-01T00:00:00Z"), Instant.parse("2024-01-01T00:00:00Z"));
    }

    @Test
    @DisplayName("GET /api/lobbies – returns list of lobbies")
    void getAll_returns200_andList() throws Exception {
        UUID ownerId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        Lobby lobby = sampleLobby(UUID.randomUUID(), ownerId);

        when(lobbyService.findAllLobbies()).thenReturn(List.of(lobby));
        mockMvc.perform(get("/api/lobbies").with(jwtFor(ownerId.toString(), "user", "user@kdg.be"))).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(lobby.id().value().toString()));
    }

}
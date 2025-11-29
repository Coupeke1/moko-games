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
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.PlayerName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InviteController.class)
@Import(TestSecurityConfig.class)
class InviteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LobbyService lobbyService;
    @MockitoBean
    private LobbyPlayerService lobbyPlayerService;
    @MockitoBean
    private InviteQueryService inviteQueryService;
    @MockitoBean
    private PlayerService playerService;
    @MockitoBean
    private JwtDecoder jwtDecoder;

    private static RequestPostProcessor jwtFor(String sub, String username, String email) {
        return jwt().jwt(builder -> {
            builder.subject(sub);
            builder.claim("preferred_username", username);
            builder.claim("email", email);
        });
    }

    @Test
    @DisplayName("POST /api/lobbies/{id}/invite/{playerId} – calls lobbyPlayerService.invitePlayer")
    void inviteSinglePlayer_callsService() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();

        doNothing().when(lobbyPlayerService)
                .invitePlayer(any(), any(), any(), any());

        mockMvc.perform(post("/api/lobbies/{id}/invite/{playerId}", lobbyId, targetId)
                        .with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be"))
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(lobbyPlayerService).invitePlayer(
                eq(PlayerId.from(ownerId)),
                eq(LobbyId.from(lobbyId)),
                eq(PlayerId.from(targetId)),
                any(Jwt.class)
        );
    }

    @Test
    @DisplayName("POST /api/lobbies/{id}/invite/accept/me – calls lobbyPlayerService.acceptInvite")
    void acceptInvite_callsService() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        doNothing().when(lobbyPlayerService)
                .acceptInvite(eq(PlayerId.from(userId)), eq(LobbyId.from(lobbyId)), any(Jwt.class));

        mockMvc.perform(post("/api/lobbies/{id}/invite/accept/me", lobbyId)
                        .with(jwtFor(userId.toString(), "kaj", "kaj@kdg.be"))
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(lobbyPlayerService).acceptInvite(
                eq(PlayerId.from(userId)),
                eq(LobbyId.from(lobbyId)),
                any(Jwt.class)
        );
    }

    @Test
    @DisplayName("GET /api/lobbies/{id}/invited/{userId} – returns true when invited")
    void isPlayerInvited_true() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID invitedId = UUID.randomUUID();

        Lobby lobbyMock = mock(Lobby.class);
        when(lobbyMock.invitedPlayers()).thenReturn(Set.of(PlayerId.from(invitedId)));
        when(lobbyService.findLobby(LobbyId.from(lobbyId))).thenReturn(lobbyMock);

        mockMvc.perform(get("/api/lobbies/{lobbyId}/invited/{userId}", lobbyId, invitedId)
                        .with(jwtFor("x", "x", "x"))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(lobbyService).findLobby(LobbyId.from(lobbyId));
    }

    @Test
    @DisplayName("GET /api/lobbies/{id}/invited/{userId} – returns false when NOT invited")
    void isPlayerInvited_false() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();

        Lobby lobbyMock = mock(Lobby.class);
        when(lobbyMock.invitedPlayers()).thenReturn(Set.of());
        when(lobbyService.findLobby(LobbyId.from(lobbyId))).thenReturn(lobbyMock);

        mockMvc.perform(get("/api/lobbies/{lobbyId}/invited/{userId}", lobbyId, otherId)
                        .with(jwtFor("y", "y", "y"))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("POST /api/lobbies/{id}/invite/bot – calls lobbyPlayerService.addBot and returns updated lobby")
    void inviteBot_callsService_andReturnsLobby() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        PlayerId ownerPid = PlayerId.from(ownerId);

        var settings = new LobbySettings(new TicTacToeSettings(3), 2);

        Lobby lobby = new Lobby(
                LobbyId.from(lobbyId),
                GameId.from(UUID.randomUUID()),
                ownerPid,
                List.of(new Player(ownerPid, new PlayerName("owner"), "", true)),
                Set.of(),
                settings,
                LobbyStatus.OPEN,
                Instant.now(),
                Instant.now(),
                null
        );

        doNothing().when(lobbyPlayerService).addBot(ownerPid, LobbyId.from(lobbyId));
        when(lobbyService.findLobby(LobbyId.from(lobbyId))).thenReturn(lobby);

        mockMvc.perform(post("/api/lobbies/{id}/invite/bot", lobbyId)
                        .with(jwtFor(ownerId.toString(), "owner", "owner@kdg.be"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lobbyId.toString()))
                .andExpect(jsonPath("$.ownerId").value(ownerId.toString()));

        verify(lobbyPlayerService).addBot(ownerPid, LobbyId.from(lobbyId));
        verify(lobbyService, atLeastOnce()).findLobby(LobbyId.from(lobbyId));
    }

}
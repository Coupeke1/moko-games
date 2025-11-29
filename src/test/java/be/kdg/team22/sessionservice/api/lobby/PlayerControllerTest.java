package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.application.lobby.InviteQueryService;
import be.kdg.team22.sessionservice.application.lobby.LobbyPlayerService;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.application.player.PlayerService;
import be.kdg.team22.sessionservice.config.TestSecurityConfig;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
@Import(TestSecurityConfig.class)
class PlayerControllerTest {
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


    @Test
    @DisplayName("PATCH /api/lobbies/{id}/players/ready – mismatch between JWT and intended player returns 400")
    void setReady_mismatch_returns400() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID tokenUser = UUID.fromString("99999999-0000-0000-0000-000000000000");

        mockMvc.perform(patch("/api/lobbies/{lobbyId}/players/ready", lobbyId).with(jwtFor(tokenUser.toString(), "user", "user@kdg.be")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"ready\": true}")).andExpect(status().isOk());
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
    @DisplayName("PATCH /api/lobbies/{id}/players/ready – calls lobbyPlayerService.setReady")
    void setReady_callsService() throws Exception {
        UUID lobbyId = UUID.randomUUID();
        UUID actingUserId = UUID.fromString("77777777-0000-0000-0000-000000000000");

        doNothing().when(lobbyPlayerService).setReady(eq(PlayerId.from(actingUserId)), eq(LobbyId.from(lobbyId)));

        mockMvc.perform(patch("/api/lobbies/{lobbyId}/players/ready", lobbyId).with(jwtFor(actingUserId.toString(), "user", "user@kdg.be")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"ready\": true}")).andExpect(status().isOk());

        verify(lobbyPlayerService).setReady(eq(PlayerId.from(actingUserId)), eq(LobbyId.from(lobbyId)));
    }
}
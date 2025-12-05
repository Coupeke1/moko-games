package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.LobbyModel;
import be.kdg.team22.sessionservice.application.lobby.LobbyPlayerService;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/lobbies")
public class PlayerController {
    private final LobbyPlayerService lobbyPlayerService;
    private final LobbyService lobbyService;

    public PlayerController(final LobbyPlayerService lobbyPlayerService, LobbyService lobbyService) {
        this.lobbyPlayerService = lobbyPlayerService;
        this.lobbyService = lobbyService;
    }

    @DeleteMapping("/{lobbyId}/players/{playerId}")
    public void removePlayer(@PathVariable final UUID lobbyId, @PathVariable final UUID playerId, @AuthenticationPrincipal final Jwt token) {
        lobbyPlayerService.removePlayer(PlayerId.get(token), LobbyId.from(lobbyId), PlayerId.from(playerId));
    }

    @PatchMapping("/{lobbyId}/players/ready")
    public void setReady(@PathVariable final UUID lobbyId, @AuthenticationPrincipal final Jwt token) {
        lobbyPlayerService.setReady(PlayerId.get(token), LobbyId.from(lobbyId));
    }

    @PatchMapping("/{lobbyId}/players/unready")
    public void setUnready(@PathVariable final UUID lobbyId, @AuthenticationPrincipal final Jwt token) {
        lobbyPlayerService.setUnready(PlayerId.get(token), LobbyId.from(lobbyId));
    }


    @DeleteMapping("/{id}/bot")
    public ResponseEntity<LobbyModel> removeBot(@PathVariable UUID id, @AuthenticationPrincipal Jwt token) {
        PlayerId ownerId = PlayerId.get(token);
        LobbyId lobbyId = LobbyId.from(id);

        lobbyPlayerService.removeBot(ownerId, lobbyId);

        Lobby updatedLobby = lobbyService.findLobby(lobbyId);
        return ResponseEntity.ok(LobbyModel.from(updatedLobby));
    }
}
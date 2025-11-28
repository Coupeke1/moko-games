package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.application.lobby.LobbyPlayerService;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyPlayerController {

    private final LobbyPlayerService lobbyPlayerService;

    public LobbyPlayerController(final LobbyPlayerService lobbyPlayerService) {
        this.lobbyPlayerService = lobbyPlayerService;
    }

    @DeleteMapping("/{lobbyId}/players/{playerId}")
    public void removePlayer(
            @PathVariable final UUID lobbyId,
            @PathVariable final UUID playerId,
            @AuthenticationPrincipal final Jwt token
    ) {
        lobbyPlayerService.removePlayer(
                PlayerId.get(token),
                LobbyId.from(lobbyId),
                PlayerId.from(playerId)
        );
    }

    @PatchMapping("/{lobbyId}/players/ready")
    public void setReady(
            @PathVariable final UUID lobbyId,
            @AuthenticationPrincipal final Jwt token
    ) {
        lobbyPlayerService.setReady(PlayerId.get(token), LobbyId.from(lobbyId));
    }

    @PatchMapping("/{lobbyId}/players/unready")
    public void setUnready(
            @PathVariable final UUID lobbyId,
            @AuthenticationPrincipal final Jwt token
    ) {
        lobbyPlayerService.setUnready(PlayerId.get(token), LobbyId.from(lobbyId));
    }
}


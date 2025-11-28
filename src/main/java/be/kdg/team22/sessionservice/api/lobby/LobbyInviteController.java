package be.kdg.team22.sessionservice.api.lobby;

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
public class LobbyInviteController {
    private final LobbyPlayerService lobbyPlayerService;
    private final LobbyService lobbyService;

    public LobbyInviteController(
            final LobbyPlayerService lobbyPlayerService,
            final LobbyService lobbyService
    ) {
        this.lobbyPlayerService = lobbyPlayerService;
        this.lobbyService = lobbyService;
    }

    @PostMapping("/{id}/invite/{playerId}")
    public void invitePlayer(
            @PathVariable UUID id,
            @PathVariable UUID playerId,
            @AuthenticationPrincipal Jwt token
    ) {
        lobbyPlayerService.invitePlayer(
                PlayerId.get(token),
                LobbyId.from(id),
                PlayerId.from(playerId),
                token
        );
    }

    @PostMapping("/{id}/invite/accept/me")
    public void acceptInvite(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt token
    ) {
        lobbyPlayerService.acceptInvite(PlayerId.get(token), LobbyId.from(id), token);
    }

    @GetMapping("/{lobbyId}/invited/{userId}")
    public ResponseEntity<Boolean> isPlayerInvited(
            @PathVariable UUID lobbyId,
            @PathVariable UUID userId
    ) {
        Lobby lobby = lobbyService.findLobby(LobbyId.from(lobbyId));
        return ResponseEntity.ok(lobby.invitedPlayers().contains(PlayerId.from(userId)));
    }

    @PostMapping("/{id}/invite/bot")
    public ResponseEntity<Void> inviteBot(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt token
    ) {
        lobbyPlayerService.addBot(
                PlayerId.get(token),
                LobbyId.from(id)
        );

        return ResponseEntity.ok().build();
    }
}
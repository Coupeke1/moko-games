package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.LobbyResponseModel;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lobbies")
public class QueryController {
    private final LobbyService service;

    public QueryController(final LobbyService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<LobbyResponseModel>> getAll() {
        return ResponseEntity.ok(service.findAllLobbies().stream().map(LobbyResponseModel::from).toList());
    }

    @GetMapping("/{lobbyId}/invited")
    public ResponseEntity<List<UUID>> getInvitedPlayers(@PathVariable UUID lobbyId) {
        Lobby lobby = service.findLobby(LobbyId.from(lobbyId));
        return ResponseEntity.ok(lobby.invitedPlayers().stream().map(PlayerId::value).toList());
    }

    @GetMapping("/invited/{gameId}/me")
    public ResponseEntity<List<LobbyResponseModel>> getInvitesForPlayer(@AuthenticationPrincipal Jwt token, @PathVariable UUID gameId) {
        PlayerId player = PlayerId.get(token);
        GameId game = new GameId(gameId);

        return ResponseEntity.ok(service.getInvitesFromPlayer(player, game).stream().map(LobbyResponseModel::from).toList());
    }
}

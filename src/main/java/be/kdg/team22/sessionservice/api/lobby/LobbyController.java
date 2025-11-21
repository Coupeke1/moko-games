package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.*;
import be.kdg.team22.sessionservice.application.lobby.LobbyPlayerService;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.settings.CheckersSettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {

    private final LobbyService service;
    private final LobbyPlayerService playerService;

    public LobbyController(
            LobbyService service,
            LobbyPlayerService playerService
    ) {
        this.service = service;
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<LobbyResponseModel> create(
            @RequestBody final CreateLobbyModel model,
            @AuthenticationPrincipal final Jwt token
    ) {
        PlayerId ownerId = PlayerId.get(token);
        GameId gameId = new GameId(model.gameId());

        Lobby lobby = service.createLobby(gameId, ownerId, model);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseModel(lobby));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LobbyResponseModel> getById(@PathVariable final UUID id) {
        Lobby lobby = service.findLobby(LobbyId.from(id));
        return ResponseEntity.ok(toResponseModel(lobby));
    }

    @GetMapping
    public ResponseEntity<List<LobbyResponseModel>> getAll() {
        List<Lobby> lobbies = service.findAllLobbies();
        return ResponseEntity.ok(lobbies.stream()
                .map(this::toResponseModel)
                .toList());
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<LobbyResponseModel> close(
            @PathVariable final UUID id,
            @AuthenticationPrincipal final Jwt jwt
    ) {
        PlayerId actingUser = PlayerId.get(jwt);
        Lobby lobby = service.closeLobby(LobbyId.from(id), actingUser);
        return ResponseEntity.ok(toResponseModel(lobby));
    }

    @PutMapping("/{id}/settings")
    public ResponseEntity<LobbyResponseModel> updateSettings(
            @PathVariable final UUID id,
            @RequestBody final UpdateLobbySettingsModel model,
            @AuthenticationPrincipal final Jwt jwt
    ) {
        PlayerId actingUser = PlayerId.get(jwt);
        Lobby lobby = service.updateSettings(LobbyId.from(id), actingUser, model);
        return ResponseEntity.ok(toResponseModel(lobby));
    }

    @PostMapping("/{lobbyId}/invite/{playerId}")
    public void invitePlayer(
            @PathVariable UUID lobbyId,
            @PathVariable UUID playerId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        playerService.invitePlayer(ownerId, lobbyId, playerId);
    }

    @PostMapping("/{lobbyId}/invite")
    public void invitePlayers(
            @PathVariable UUID lobbyId,
            @RequestBody InvitePlayersRequestModel request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        playerService.invitePlayers(ownerId, lobbyId, request.playerIds());
    }

    @PostMapping("/{lobbyId}/players/{playerId}")
    public void acceptInvite(
            @PathVariable UUID lobbyId,
            @PathVariable UUID playerId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID actingUser = UUID.fromString(jwt.getSubject());
        if (!actingUser.equals(playerId)) {
            throw new IllegalArgumentException("Authenticated user does not match playerId in path.");
        }
        playerService.acceptInvite(actingUser, lobbyId);
    }

    @DeleteMapping("/{lobbyId}/players/{playerId}")
    public void removePlayer(
            @PathVariable UUID lobbyId,
            @PathVariable UUID playerId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        playerService.removePlayer(ownerId, lobbyId, playerId);
    }

    @DeleteMapping("/{lobbyId}/players")
    public void removePlayers(
            @PathVariable UUID lobbyId,
            @RequestBody RemovePlayersRequestModel request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        playerService.removePlayers(ownerId, lobbyId, request.playerIds());
    }

    private LobbyResponseModel toResponseModel(final Lobby lobby) {

        GameSettingsModel settingsModel = switch (lobby.settings().gameSettings()) {
            case TicTacToeSettings t -> new TicTacToeSettingsModel(t.boardSize());
            case CheckersSettings c -> new CheckersSettingsModel(c.boardSize(), c.flyingKings());
        };

        return new LobbyResponseModel(
                lobby.id().value(),
                lobby.gameId().value(),
                lobby.ownerId().value(),
                lobby.players().stream()
                        .map(LobbyPlayer::id)
                        .collect(Collectors.toSet()),
                lobby.settings().maxPlayers(),
                lobby.status(),
                lobby.createdAt(),
                settingsModel
        );
    }
}

package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.*;
import be.kdg.team22.sessionservice.application.lobby.LobbyInviteQueryService;
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
    private final LobbyInviteQueryService queryService;

    public LobbyController(
            LobbyService service,
            LobbyPlayerService playerService,
            LobbyInviteQueryService queryService
    ) {
        this.service = service;
        this.playerService = playerService;
        this.queryService = queryService;
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
    public ResponseEntity<LobbyResponseModel> getById(@PathVariable UUID id) {
        Lobby lobby = service.findLobby(LobbyId.from(id));
        return ResponseEntity.ok(toResponseModel(lobby));
    }

    @GetMapping
    public ResponseEntity<List<LobbyResponseModel>> getAll() {
        return ResponseEntity.ok(
                service.findAllLobbies()
                        .stream()
                        .map(this::toResponseModel)
                        .toList()
        );
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<LobbyResponseModel> close(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt token
    ) {
        PlayerId actingUser = PlayerId.get(token);
        Lobby lobby = service.closeLobby(LobbyId.from(id), actingUser);
        return ResponseEntity.ok(toResponseModel(lobby));
    }

    @PutMapping("/{id}/settings")
    public ResponseEntity<LobbyResponseModel> updateSettings(
            @PathVariable UUID id,
            @RequestBody UpdateLobbySettingsModel model,
            @AuthenticationPrincipal Jwt token
    ) {
        PlayerId actingUser = PlayerId.get(token);
        Lobby lobby = service.updateSettings(LobbyId.from(id), actingUser, model);
        return ResponseEntity.ok(toResponseModel(lobby));
    }

    @PostMapping("/{lobbyId}/invite/{playerId}")
    public void invitePlayer(
            @PathVariable UUID lobbyId,
            @PathVariable UUID playerId,
            @AuthenticationPrincipal Jwt token
    ) {
        PlayerId ownerId = PlayerId.get(token);
        PlayerId targetId = PlayerId.from(playerId);

        playerService.invitePlayer(ownerId, LobbyId.from(lobbyId), targetId, token);
    }

    @PostMapping("/{lobbyId}/invite")
    public void invitePlayers(
            @PathVariable UUID lobbyId,
            @RequestBody InvitePlayersRequestModel request,
            @AuthenticationPrincipal Jwt token
    ) {
        PlayerId ownerId = PlayerId.get(token);

        List<PlayerId> ids = request.playerIds().stream()
                .map(PlayerId::from)
                .toList();

        playerService.invitePlayers(ownerId, LobbyId.from(lobbyId), ids, token);
    }

    @PostMapping("/{lobbyId}/players/{playerId}")
    public void acceptInvite(
            @PathVariable UUID lobbyId,
            @PathVariable UUID playerId,
            @AuthenticationPrincipal Jwt token
    ) {
        PlayerId actingUser = PlayerId.get(token);
        if (!actingUser.value().equals(playerId)) {
            throw new IllegalArgumentException("Authenticated user does not match path playerId.");
        }

        playerService.acceptInvite(actingUser, LobbyId.from(lobbyId), token.getTokenValue());
    }

    @DeleteMapping("/{lobbyId}/players/{playerId}")
    public void removePlayer(
            @PathVariable UUID lobbyId,
            @PathVariable UUID playerId,
            @AuthenticationPrincipal Jwt token
    ) {
        PlayerId ownerId = PlayerId.get(token);
        playerService.removePlayer(ownerId, LobbyId.from(lobbyId), PlayerId.from(playerId));
    }

    @DeleteMapping("/{lobbyId}/players")
    public void removePlayers(
            @PathVariable UUID lobbyId,
            @RequestBody RemovePlayersRequestModel request,
            @AuthenticationPrincipal Jwt token
    ) {
        PlayerId ownerId = PlayerId.get(token);

        List<PlayerId> ids = request.playerIds().stream()
                .map(PlayerId::from)
                .toList();

        playerService.removePlayers(ownerId, LobbyId.from(lobbyId), ids);
    }

    @GetMapping("/invited")
    public ResponseEntity<List<LobbyInviteModel>> getInvited(@AuthenticationPrincipal Jwt token) {
        PlayerId userId = PlayerId.get(token);
        return ResponseEntity.ok(queryService.getInvitesForUser(userId, token.getTokenValue()));
    }

    private LobbyResponseModel toResponseModel(final Lobby lobby) {
        GameSettingsModel model = switch (lobby.settings().gameSettings()) {
            case TicTacToeSettings t -> new TicTacToeSettingsModel(t.boardSize());
            case CheckersSettings c -> new CheckersSettingsModel(c.boardSize(), c.flyingKings());
        };

        return new LobbyResponseModel(
                lobby.id().value(),
                lobby.gameId().value(),
                lobby.ownerId().value(),
                lobby.players().stream().map(LobbyPlayer::id).collect(Collectors.toSet()),
                lobby.settings().maxPlayers(),
                lobby.status(),
                lobby.createdAt(),
                model
        );
    }
}
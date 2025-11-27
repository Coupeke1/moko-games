package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.*;
import be.kdg.team22.sessionservice.application.lobby.LobbyInviteQueryService;
import be.kdg.team22.sessionservice.application.lobby.LobbyPlayerService;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.application.player.PlayerService;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.InviteNotFoundException;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {

    private final LobbyService lobbyService;
    private final LobbyPlayerService lobbyPlayerService;
    private final LobbyInviteQueryService queryService;
    private final PlayerService playerService;

    public LobbyController(
            final LobbyService lobbyService,
            final LobbyPlayerService lobbyPlayerService,
            final LobbyInviteQueryService queryService,
            final PlayerService playerService
    ) {
        this.lobbyService = lobbyService;
        this.lobbyPlayerService = lobbyPlayerService;
        this.queryService = queryService;
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<LobbyResponseModel> create(
            @RequestBody final CreateLobbyModel model,
            @AuthenticationPrincipal final Jwt token
    ) {
        PlayerId ownerId = PlayerId.get(token);
        GameId gameId = new GameId(model.gameId());

        Lobby lobby = lobbyService.createLobby(gameId, ownerId, model, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(LobbyResponseModel.from(lobby));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LobbyResponseModel> getById(@PathVariable final UUID id) {
        Lobby lobby = lobbyService.findLobby(LobbyId.from(id));
        return ResponseEntity.ok(LobbyResponseModel.from(lobby));
    }

    @GetMapping
    public ResponseEntity<List<LobbyResponseModel>> getAll() {
        List<Lobby> lobbies = lobbyService.findAllLobbies();
        return ResponseEntity.ok(lobbies.stream()
                .map(LobbyResponseModel::from)
                .toList());
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<LobbyResponseModel> close(
            @PathVariable final UUID id,
            @AuthenticationPrincipal final Jwt token
    ) {
        PlayerId ownerId = PlayerId.get(token);
        Lobby lobby = lobbyService.closeLobby(LobbyId.from(id), ownerId);

        return ResponseEntity.ok(LobbyResponseModel.from(lobby));
    }

    @PutMapping("/{id}/settings")
    public ResponseEntity<LobbyResponseModel> updateSettings(
            @PathVariable final UUID id,
            @RequestBody final UpdateLobbySettingsModel model,
            @AuthenticationPrincipal final Jwt token
    ) {
        PlayerId player = PlayerId.get(token);
        Lobby lobby = lobbyService.updateSettings(LobbyId.from(id), player, model);

        return ResponseEntity.ok(LobbyResponseModel.from(lobby));
    }

    @PostMapping("/{id}/invite/{playerId}")
    public void invitePlayer(
            @PathVariable UUID id,
            @PathVariable final UUID playerId,
            @AuthenticationPrincipal final Jwt token
    ) {
        PlayerId owner = PlayerId.get(token);
        lobbyPlayerService.invitePlayer(owner, LobbyId.from(id), PlayerId.from(playerId), token);
    }

    @PostMapping("/{id}/invite")
    public void invitePlayers(
            @PathVariable final UUID id,
            @RequestBody final InvitePlayersRequestModel request,
            @AuthenticationPrincipal final Jwt token
    ) {
        PlayerId owner = PlayerId.get(token);
        List<PlayerId> players = request.playerIds().stream()
                .map(PlayerId::from)
                .toList();

        lobbyPlayerService.invitePlayers(owner, LobbyId.from(id), players, token);
    }

    @PostMapping("/{id}/players/{playerId}")
    public void acceptInvite(
            @PathVariable final UUID id,
            @PathVariable final UUID playerId,
            @AuthenticationPrincipal final Jwt token
    ) {
        LobbyId lobby = LobbyId.from(id);
        PlayerId player = PlayerId.get(token);

        if (!player.value().equals(playerId)) {
            throw new InviteNotFoundException(lobby, player);
        }

        lobbyPlayerService.acceptInvite(player, lobby, token);
    }

    @GetMapping("/{lobbyId}/invited")
    public ResponseEntity<List<UUID>> getInvitedFromLobby(
            @PathVariable UUID lobbyId
    ) {
        Lobby lobby = lobbyService.findLobby(new LobbyId(lobbyId));

        return ResponseEntity.ok(
                lobby.invitedPlayers().stream()
                        .map(PlayerId::value)
                        .toList()
        );
    }

    @GetMapping("/{lobbyId}/invited/{userId}")
    public ResponseEntity<Boolean> IsPlayerInvitedForLobby(
            @AuthenticationPrincipal final Jwt token,
            @PathVariable final UUID lobbyId,
            @PathVariable final UUID userId
    ) {
        PlayerId player = PlayerId.from(userId);
        Lobby lobby = lobbyService.findLobby(LobbyId.from(lobbyId));
        return ResponseEntity.ok(lobby.invitedPlayers().contains(player));
    }

    @DeleteMapping("/{lobbyId}/players/{playerId}")
    public void removePlayer(
            @PathVariable final UUID lobbyId,
            @PathVariable final UUID playerId,
            @AuthenticationPrincipal final Jwt token
    ) {
        PlayerId owner = PlayerId.get(token);
        lobbyPlayerService.removePlayer(owner, LobbyId.from(lobbyId), PlayerId.from(playerId));
    }

    @DeleteMapping("/{id}/players")
    public void removePlayers(
            @PathVariable final UUID id,
            @RequestBody final RemovePlayersRequestModel request,
            @AuthenticationPrincipal final Jwt token
    ) {
        PlayerId owner = PlayerId.get(token);
        List<PlayerId> players = request.playerIds().stream()
                .map(PlayerId::from)
                .toList();

        lobbyPlayerService.removePlayers(owner, LobbyId.from(id), players);
    }

    @PostMapping("/{lobbyId}/start")
    public ResponseEntity<LobbyResponseModel> start(
            @PathVariable final UUID lobbyId,
            @AuthenticationPrincipal final Jwt token
    ) {
        PlayerId owner = PlayerId.get(token);
        Lobby lobby = lobbyService.startLobby(LobbyId.from(lobbyId), owner, token);
        return ResponseEntity.ok(LobbyResponseModel.from(lobby));
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
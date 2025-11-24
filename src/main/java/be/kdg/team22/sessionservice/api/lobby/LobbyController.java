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
import be.kdg.team22.sessionservice.domain.lobby.settings.CheckersSettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {
    private final LobbyService lobbyService;
    private final LobbyPlayerService lobbyPlayerService;
    private final LobbyInviteQueryService queryService;
    private final PlayerService playerService;

    public LobbyController(final LobbyService lobbyService, final LobbyPlayerService lobbyPlayerService, final LobbyInviteQueryService queryService, final PlayerService playerService) {
        this.lobbyService = lobbyService;
        this.lobbyPlayerService = lobbyPlayerService;
        this.queryService = queryService;
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<LobbyResponseModel> create(@RequestBody final CreateLobbyModel model, @AuthenticationPrincipal final Jwt token) {
        PlayerId ownerId = PlayerId.get(token);
        GameId gameId = new GameId(model.gameId());

        Lobby lobby = lobbyService.createLobby(gameId, ownerId, model, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseModel(lobby));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LobbyResponseModel> getById(@PathVariable final UUID id) {
        Lobby lobby = lobbyService.findLobby(LobbyId.from(id));
        return ResponseEntity.ok(toResponseModel(lobby));
    }

    @GetMapping
    public ResponseEntity<List<LobbyResponseModel>> getAll() {
        List<Lobby> lobbies = lobbyService.findAllLobbies();
        return ResponseEntity.ok(lobbies.stream().map(this::toResponseModel).toList());
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<LobbyResponseModel> close(@PathVariable final UUID id, @AuthenticationPrincipal final Jwt token) {
        PlayerId ownerId = PlayerId.get(token);
        Lobby lobby = lobbyService.closeLobby(LobbyId.from(id), ownerId);

        return ResponseEntity.ok(toResponseModel(lobby));
    }

    @PutMapping("/{id}/settings")
    public ResponseEntity<LobbyResponseModel> updateSettings(@PathVariable final UUID id, @RequestBody final UpdateLobbySettingsModel model, @AuthenticationPrincipal final Jwt token) {
        PlayerId player = PlayerId.get(token);
        Lobby lobby = lobbyService.updateSettings(LobbyId.from(id), player, model);

        return ResponseEntity.ok(toResponseModel(lobby));
    }

    @PostMapping("/{id}/invite/{playerId}")
    public void invitePlayer(@PathVariable UUID id, @PathVariable final UUID playerId, @AuthenticationPrincipal final Jwt token) {
        PlayerId owner = PlayerId.get(token);
        PlayerId player = PlayerId.from(playerId);

        lobbyPlayerService.invitePlayer(owner, LobbyId.from(id), player, token);
    }

    @PostMapping("/{id}/invite")
    public void invitePlayers(@PathVariable final UUID id, @RequestBody final InvitePlayersRequestModel request, @AuthenticationPrincipal final Jwt token) {
        PlayerId owner = PlayerId.get(token);
        List<PlayerId> players = request.playerIds().stream().map(PlayerId::from).toList();

        lobbyPlayerService.invitePlayers(owner, LobbyId.from(id), players, token);
    }

    @PostMapping("/{id}/players/{playerId}")
    public void acceptInvite(@PathVariable final UUID id, @PathVariable final UUID playerId, @AuthenticationPrincipal final Jwt token) {
        LobbyId lobby = LobbyId.from(id);
        PlayerId player = PlayerId.get(token);

        if (!player.value().equals(playerId))
            throw new InviteNotFoundException(lobby, player);

        lobbyPlayerService.acceptInvite(player, LobbyId.from(id), token);
    }

    @DeleteMapping("/{lobbyId}/players/{playerId}")
    public void removePlayer(@PathVariable final UUID lobbyId, @PathVariable final UUID playerId, @AuthenticationPrincipal final Jwt token) {
        PlayerId owner = PlayerId.get(token);
        lobbyPlayerService.removePlayer(owner, LobbyId.from(lobbyId), PlayerId.from(playerId));
    }

    @DeleteMapping("/{id}/players")
    public void removePlayers(@PathVariable final UUID id, @RequestBody final RemovePlayersRequestModel request, @AuthenticationPrincipal final Jwt token) {
        PlayerId owner = PlayerId.get(token);
        List<PlayerId> players = request.playerIds().stream().map(PlayerId::from).toList();

        lobbyPlayerService.removePlayers(owner, LobbyId.from(id), players);
    }

    @GetMapping("/invited")
    public ResponseEntity<List<LobbyInviteModel>> getInvited(@AuthenticationPrincipal final Jwt token) {
        PlayerId player = PlayerId.get(token);
        List<Lobby> lobbies = queryService.getInvitesForPlayer(player);
        return ResponseEntity.ok(lobbies.stream().map(lobby -> toInviteModel(lobby, token)).toList());
    }

    private LobbyResponseModel toResponseModel(final Lobby lobby) {
        GameSettingsModel model = switch (lobby.settings().gameSettings()) {
            case TicTacToeSettings t ->
                    new TicTacToeSettingsModel(t.boardSize());
            case CheckersSettings c ->
                    new CheckersSettingsModel(c.boardSize(), c.flyingKings());
        };

        Set<UUID> players = lobby.players().stream().map(player -> player.id().value()).collect(Collectors.toSet());
        return new LobbyResponseModel(lobby.id().value(), lobby.gameId().value(), lobby.ownerId().value(), players, lobby.settings().maxPlayers(), lobby.status(), lobby.createdAt(), model);
    }

    private LobbyInviteModel toInviteModel(final Lobby lobby, final Jwt token) {
        Player owner = playerService.findPlayer(PlayerId.get(token), token);
        Set<PlayerSummaryModel> players = lobby.players().stream().map(this::toPlayerModel).collect(Collectors.toSet());

        Set<PlayerSummaryModel> invited = lobby.invitedPlayers().stream().map(playerId -> {
            Player player = playerService.findPlayer(playerId, token);
            return new PlayerSummaryModel(player.id().value(), player.username().value());
        }).collect(Collectors.toSet());

        return new LobbyInviteModel(lobby.id().value(), lobby.gameId().value(), "TODO_GAME_NAME", owner.id().value(), owner.username().value(), players, invited, lobby.settings().maxPlayers(), lobby.status().name(), lobby.createdAt());
    }

    private PlayerSummaryModel toPlayerModel(final Player player) {
        return new PlayerSummaryModel(player.id().value(), player.username().value());
    }
}
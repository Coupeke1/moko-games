package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.CreateLobbyModel;
import be.kdg.team22.sessionservice.api.lobby.models.LobbyResponseModel;
import be.kdg.team22.sessionservice.api.lobby.models.UpdateLobbySettingsModel;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.domain.lobby.*;
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
    private final LobbyService service;

    public LobbyController(LobbyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LobbyResponseModel> create(@RequestBody final CreateLobbyModel model, @AuthenticationPrincipal final Jwt token) {
        PlayerId ownerId = PlayerId.get(token);
        GameId gameId = new GameId(model.gameId());
        Lobby lobby = service.createLobby(gameId, ownerId);

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
        List<LobbyResponseModel> response = lobbies.stream().map(this::toResponseModel).toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<LobbyResponseModel> close(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        PlayerId actingUser = PlayerId.get(jwt);
        Lobby lobby = lobbyService.closeLobby(LobbyId.from(id), actingUser);
        return ResponseEntity.ok(toResponseModel(lobby));
    }

    @PutMapping("/{id}/settings")
    public ResponseEntity<LobbyResponseModel> updateSettings(
            @PathVariable UUID id,
            @RequestBody UpdateLobbySettingsModel model,
            @AuthenticationPrincipal Jwt jwt
    ) {
        PlayerId actingUser = PlayerId.get(jwt);
        LobbySettings settings = new LobbySettings(model.maxPlayers());
        Lobby lobby = lobbyService.updateSettings(LobbyId.from(id), actingUser, settings);
        return ResponseEntity.ok(toResponseModel(lobby));
    }

    private LobbyResponseModel toResponseModel(finalLobby lobby) {
        Set<UUID> players = lobby.players()
                .stream()
                .map(PlayerId::value)
                .collect(Collectors.toSet());

        return new LobbyResponseModel(
                lobby.id().value(),
                lobby.gameId().value(),
                lobby.ownerId().value(),
                players,
                lobby.settings().maxPlayers(),
                lobby.status(),
                lobby.createdAt()
        );
    }
}
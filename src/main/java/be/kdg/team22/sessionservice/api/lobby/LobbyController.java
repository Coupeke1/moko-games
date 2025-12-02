package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.CreateLobbyModel;
import be.kdg.team22.sessionservice.api.lobby.models.LobbyResponseModel;
import be.kdg.team22.sessionservice.api.lobby.models.UpdateLobbySettingsModel;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {
    private final LobbyService service;

    public LobbyController(final LobbyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LobbyResponseModel> create(@RequestBody final CreateLobbyModel model, @AuthenticationPrincipal final Jwt token) {
        PlayerId owner = PlayerId.get(token);
        GameId game = new GameId(model.gameId());

        Lobby lobby = service.createLobby(game, owner, model, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(LobbyResponseModel.from(lobby));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LobbyResponseModel> getById(@PathVariable final UUID id) {
        Lobby lobby = service.findLobby(LobbyId.from(id));
        return ResponseEntity.ok(LobbyResponseModel.from(lobby));
    }

    @PutMapping("/{id}/settings")
    public ResponseEntity<LobbyResponseModel> updateSettings(@PathVariable final UUID id, @RequestBody final UpdateLobbySettingsModel model, @AuthenticationPrincipal final Jwt token) {
        Lobby lobby = service.updateSettings(LobbyId.from(id), PlayerId.get(token), model);
        return ResponseEntity.ok(LobbyResponseModel.from(lobby));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<LobbyResponseModel> close(@PathVariable final UUID id, @AuthenticationPrincipal final Jwt token) {
        Lobby lobby = service.closeLobby(LobbyId.from(id), PlayerId.get(token));
        return ResponseEntity.ok(LobbyResponseModel.from(lobby));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<String> start(
            @PathVariable final UUID id,
            @AuthenticationPrincipal final Jwt token
    ) {
        Lobby lobby = service.startLobby(LobbyId.from(id), PlayerId.get(token), token);

        //TODO fix custom exception
        UUID instanceId = lobby.startedGameId()
                .map(GameId::value)
                .orElseThrow(() -> new IllegalStateException("Game did not start"));

        return ResponseEntity.ok(instanceId.toString());
    }
}
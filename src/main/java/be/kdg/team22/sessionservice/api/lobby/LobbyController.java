package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.CreateLobbyModel;
import be.kdg.team22.sessionservice.api.lobby.models.LobbyResponseModel;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.PlayerId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {

    private final LobbyService lobbyService;

    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @PostMapping
    public ResponseEntity<LobbyResponseModel> create(
            @RequestBody CreateLobbyModel model,
            @AuthenticationPrincipal Jwt jwt
    ) {
        PlayerId ownerId = PlayerId.get(jwt);
        GameId gameId = new GameId(model.gameId());
        Lobby lobby = lobbyService.createLobby(
                gameId,
                ownerId
        );

        Set<UUID> players = lobby.players()
                .stream()
                .map(PlayerId::value)
                .collect(Collectors.toSet());

        LobbyResponseModel response = new LobbyResponseModel(
                lobby.id().value(),
                lobby.gameId().value(),
                lobby.ownerId().value(),
                players,
                lobby.status().name(),
                lobby.createdAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}

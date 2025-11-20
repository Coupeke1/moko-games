package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.CreateLobbyModel;
import be.kdg.team22.sessionservice.api.lobby.models.LobbyResponseModel;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.PlayerId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/create")
    public ResponseEntity<LobbyResponseModel> create(@RequestBody CreateLobbyModel model) {
        Lobby lobby = lobbyService.createLobby(new GameId(model.gameId()), new PlayerId(model.ownerId())
        );

        Set<UUID> players = lobby.players()
                .stream()
                .map(PlayerId::value)
                .collect(Collectors.toSet());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LobbyResponseModel(
                        lobby.id().value(),
                        lobby.gameId().value(),
                        lobby.ownerId().value(),
                        players,
                        lobby.status().name(),
                        lobby.createdAt()
                ));
    }
}

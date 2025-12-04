package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.LobbyModel;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class LobbySocketController {
    private final LobbyService service;

    public LobbySocketController(final LobbyService service) {
        this.service = service;
    }

    @SubscribeMapping("/lobbies/{id}")
    public LobbyModel handleSubscription(@DestinationVariable final UUID id) {
        Lobby lobby = service.findLobby(new LobbyId(id));
        System.out.println("Client subscribed to lobby: " + id);
        return LobbyModel.from(lobby);
    }
}
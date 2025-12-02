package be.kdg.team22.sessionservice.infrastructure.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.LobbyModel;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class LobbySocketPublisher {
    private final SimpMessagingTemplate template;
    private final static String BASE_ROUTE = "/lobbies";

    public LobbySocketPublisher(final SimpMessagingTemplate template) {
        this.template = template;
    }

    public void publish(final Lobby lobby) {
        String route = String.format("%s/%s", BASE_ROUTE, lobby.id().value());
        LobbyModel model = LobbyModel.from(lobby);
        template.convertAndSend(route, model);
    }
}
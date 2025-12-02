package be.kdg.team22.sessionservice.infrastructure.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.LobbyModel;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class InviteSocketPublisher {
    private final SimpMessagingTemplate template;
    private final static String BASE_ROUTE = "/invites";

    public InviteSocketPublisher(final SimpMessagingTemplate template) {
        this.template = template;
    }

    public void publish(final PlayerId id, final Lobby lobby) {
        String route = String.format("%s/%s", BASE_ROUTE, id.value());
        LobbyModel model = LobbyModel.from(lobby);
        template.convertAndSend(route, model);
    }
}
package be.kdg.team22.sessionservice.infrastructure.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.LobbyModel;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class LobbySocketPublisher {
    private final SimpMessagingTemplate template;

    public LobbySocketPublisher(final SimpMessagingTemplate template) {
        this.template = template;
    }

    public void publishToLobby(final Lobby lobby) {
        String route = String.format("/topic/lobbies/%s", lobby.id().value());
        LobbyModel model = LobbyModel.from(lobby);
        template.convertAndSend(route, model);
    }

    public void publishToPlayer(final PlayerId playerId, final String message) {
        String userId = playerId.value().toString();
        template.convertAndSendToUser(userId, "/queue/notifications", message);
    }

    public void publishToPlayers(final Lobby lobby) {
        LobbyModel model = LobbyModel.from(lobby);
        lobby.players().forEach(player -> {
            String playerId = player.id().value().toString();
            template.convertAndSendToUser(playerId, "/queue/lobby", model);
        });
    }
}
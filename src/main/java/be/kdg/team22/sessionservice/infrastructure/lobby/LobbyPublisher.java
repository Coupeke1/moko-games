package be.kdg.team22.sessionservice.infrastructure.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.LobbyModel;
import be.kdg.team22.sessionservice.config.RabbitMQTopology;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class LobbyPublisher {
    private final RabbitTemplate template;

    public LobbyPublisher(final RabbitTemplate template) {
        this.template = template;
    }

    public void publishToPlayers(final Lobby lobby) {
        LobbyModel model = LobbyModel.from(lobby);
        lobby.players().forEach(player -> {
            LobbyMessage message = new LobbyMessage(player.id().value(), "lobbies", model);
            template.convertAndSend(RabbitMQTopology.EXCHANGE_USER_SOCKET, "user.message", message);
        });
    }
}
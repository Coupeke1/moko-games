package be.kdg.team22.sessionservice.infrastructure.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.LobbyModel;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.config.RabbitMQTopology;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LobbyListener {

    private final RabbitTemplate template;
    private final LobbyService service;

    public LobbyListener(
        final RabbitTemplate template,
        final LobbyService service
    ) {
        this.template = template;
        this.service = service;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_SUBSCRIBED_SOCKET)
    public void subscribeListener(final SubscribeMessage message) {
        PlayerId playerId = PlayerId.from(message.userId());

        Optional<Lobby> lobby = service.findByPlayer(playerId);
        if (lobby.isEmpty()) return;

        publishToPlayer(playerId, lobby.orElse(null));
    }

    public void publishToPlayer(final PlayerId id, final Lobby lobby) {
        LobbyModel model = LobbyModel.from(lobby);
        LobbyMessage message = LobbyMessage.of(id, model);
        template.convertAndSend(
            RabbitMQTopology.EXCHANGE_USER_SOCKET,
            "user.message",
            message
        );
    }
}

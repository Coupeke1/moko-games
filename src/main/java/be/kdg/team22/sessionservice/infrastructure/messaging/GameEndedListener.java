package be.kdg.team22.sessionservice.infrastructure.messaging;

import be.kdg.team22.sessionservice.application.lobby.PublisherService;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.config.RabbitMQTopology;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundByGameInstanceException;
import be.kdg.team22.sessionservice.infrastructure.messaging.events.GameEndedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GameEndedListener {
    private final LobbyService service;
    private final PublisherService publisher;
    private final Logger logger = LoggerFactory.getLogger(GameEndedListener.class);

    public GameEndedListener(final LobbyService service, final PublisherService publisher) {
        this.service = service;
        this.publisher = publisher;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_GAME_ENDED)
    public void handleGameEnded(final GameEndedEvent event) {
        Optional<Lobby> lobby = service.findByStartedGameId(GameId.from(event.instanceId()));
        if (lobby.isEmpty()) {
            logger.warn("Game ended but no lobby found for instanceId={}", event.instanceId());
            return;
        }

        lobby.get().finish();
        publisher.saveAndPublish(lobby.get());
    }
}
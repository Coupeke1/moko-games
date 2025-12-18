package be.kdg.team22.sessionservice.infrastructure.messaging;

import be.kdg.team22.sessionservice.application.lobby.PublisherService;
import be.kdg.team22.sessionservice.application.lobby.LobbyService;
import be.kdg.team22.sessionservice.config.RabbitMQTopology;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundByGameInstanceException;
import be.kdg.team22.sessionservice.infrastructure.messaging.events.GameEndedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class GameEndedListener {
    private final LobbyService service;
    private final PublisherService publisher;

    public GameEndedListener(final LobbyService service, final PublisherService publisher) {
        this.service = service;
        this.publisher = publisher;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_GAME_ENDED)
    public void handleGameEnded(final GameEndedEvent event) {
        Lobby lobby = service.findByStartedGameId(GameId.from(event.instanceId()))
                .orElseThrow(() -> new LobbyNotFoundByGameInstanceException(event.instanceId()));

        lobby.finish();

        publisher.saveAndPublish(lobby);
    }
}
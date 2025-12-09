package be.kdg.team22.sessionservice.infrastructure.messaging;

import be.kdg.team22.sessionservice.application.lobby.LobbyPublisherService;
import be.kdg.team22.sessionservice.config.RabbitMQTopology;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundByGameInstanceException;
import be.kdg.team22.sessionservice.infrastructure.messaging.events.GameEndedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class GameEndedListener {
    private final LobbyRepository lobbyRepository;
    private final LobbyPublisherService lobbyPublisherService;

    public GameEndedListener(final LobbyRepository lobbyRepository, final LobbyPublisherService lobbyPublisherService) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyPublisherService = lobbyPublisherService;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_GAME_ENDED)
    public void handleGameEnded(final GameEndedEvent event) {
        Lobby lobby = lobbyRepository.findByStartedGameId(GameId.from(event.instanceId()))
                .orElseThrow(() -> new LobbyNotFoundByGameInstanceException(event.instanceId()));

        lobby.finish();

        lobbyPublisherService.saveAndPublish(lobby);
    }
}
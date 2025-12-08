package be.kdg.team22.sessionservice.infrastructure.messaging;

import be.kdg.team22.sessionservice.config.RabbitMQTopology;
import be.kdg.team22.sessionservice.domain.game.GameRepository;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.infrastructure.messaging.events.LobbyInviteEvent;
import be.kdg.team22.sessionservice.infrastructure.messaging.events.PlayerJoinedLobbyEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class LobbyEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final GameRepository gameRepository;

    public LobbyEventPublisher(final RabbitTemplate rabbitTemplate, final GameRepository gameRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.gameRepository = gameRepository;
    }

    public void publishLobbyInvite(final Lobby lobby, final PlayerId inviterId, final String inviterName, final PlayerId targetUserId) {
        String gameName = gameRepository.findGameNameById(lobby.gameId());

        LobbyInviteEvent event = new LobbyInviteEvent(
                gameName,
                targetUserId.value(),
                inviterName,
                inviterId.value(),
                lobby.id().value()

        );

        rabbitTemplate.convertAndSend(
                RabbitMQTopology.EXCHANGE_SESSION,
                RabbitMQTopology.ROUTING_KEY_LOBBY_INVITE,
                event
        );
    }

    public void publishPlayerJoinedLobby(final Lobby lobby, final PlayerId playerId, final String playerName) {
        PlayerJoinedLobbyEvent event = new PlayerJoinedLobbyEvent(
                lobby.id().value(),
                playerId.value(),
                playerName,
                lobby.ownerId().value()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQTopology.EXCHANGE_SESSION,
                RabbitMQTopology.ROUTING_KEY_LOBBY_JOINED,
                event
        );
    }
}
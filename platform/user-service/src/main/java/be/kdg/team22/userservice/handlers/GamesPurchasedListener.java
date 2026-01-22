package be.kdg.team22.userservice.handlers;

import be.kdg.team22.userservice.application.library.LibraryService;
import be.kdg.team22.userservice.config.RabbitMQTopology;
import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.events.GamesPurchasedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GamesPurchasedListener {
    private static final Logger logger = LoggerFactory.getLogger(GamesPurchasedListener.class);

    private final LibraryService libraryService;

    public GamesPurchasedListener(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_GAMES_PURCHASED)
    public void handleGamesPurchased(GamesPurchasedEvent event) {
        logger.info("Received games purchased event for user {}: {} game(s)", event.userId(), event.gameIds().size());

        for (UUID gameId : event.gameIds()) {
            try {
                libraryService.addGameToLibrary(ProfileId.from(event.userId()), GameId.from(gameId));
            } catch (Exception e) {
                logger.error("Failed to add game {} to library for user {}: {}", gameId, event.userId(), e.getMessage());
            }
        }
    }
}
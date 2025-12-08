package be.kdg.team22.checkersservice.domain.events;

import org.springframework.amqp.AmqpException;

public class PublishAchievementException extends AmqpException {
    public PublishAchievementException() {
        super("Achievement could not be published");
    }
}

package be.kdg.team22.checkersservice.domain.events;

import org.springframework.amqp.AmqpException;

public class RabbitNotReachableException extends AmqpException {
    public RabbitNotReachableException() {
        super("Could not connect to RabbitMQ");
    }
}

package be.kdg.team22.tictactoeservice.domain.events.exceptions;

import org.springframework.amqp.AmqpException;

public class RabbitNotReachableException extends AmqpException {
    public RabbitNotReachableException() {
        super("Could not connect to RabbitMQ");
    }
}

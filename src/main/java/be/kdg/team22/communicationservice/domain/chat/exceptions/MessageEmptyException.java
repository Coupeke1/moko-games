package be.kdg.team22.communicationservice.domain.chat.exceptions;

public class MessageEmptyException extends RuntimeException {
    public MessageEmptyException(String message) {
        super(message);
    }
    public static MessageEmptyException BodyEmpty() {
        return new MessageEmptyException("Message body cannot be empty");
    }
}

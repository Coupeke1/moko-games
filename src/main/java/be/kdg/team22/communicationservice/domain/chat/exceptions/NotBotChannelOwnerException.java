package be.kdg.team22.communicationservice.domain.chat.exceptions;

public class NotBotChannelOwnerException extends RuntimeException {
    public NotBotChannelOwnerException(String userId, String channelId) {
        super("User " + userId + " is not the owner of bot channel " + channelId);
    }
}
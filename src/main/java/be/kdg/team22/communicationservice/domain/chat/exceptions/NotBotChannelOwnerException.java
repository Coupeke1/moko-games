package be.kdg.team22.communicationservice.domain.chat.exceptions;

import java.util.UUID;

public class NotBotChannelOwnerException extends RuntimeException {
    public NotBotChannelOwnerException(UUID userId, UUID channelId) {
        super("User " + userId + " is not the owner of bot channel " + channelId);
    }
}
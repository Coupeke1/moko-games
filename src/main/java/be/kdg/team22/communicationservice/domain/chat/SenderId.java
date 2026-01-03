package be.kdg.team22.communicationservice.domain.chat;

import be.kdg.team22.communicationservice.domain.chat.bot.BotId;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record SenderId(UUID value) {
    public static SenderId from(final UUID id) {
        return new SenderId(id);
    }

    public static SenderId from(final BotId id) {
        return new SenderId(id.value());
    }

    public static SenderId from(final UserId id) {
        return new SenderId(id.value());
    }
}
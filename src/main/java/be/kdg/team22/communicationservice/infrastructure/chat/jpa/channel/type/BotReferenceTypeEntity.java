package be.kdg.team22.communicationservice.infrastructure.chat.jpa.channel.type;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.bot.BotId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.BotReferenceType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
@DiscriminatorValue("BOT")
public class BotReferenceTypeEntity extends ReferenceTypeEntity {
    private UUID userId;
    private UUID botId;

    protected BotReferenceTypeEntity() {}

    public BotReferenceTypeEntity(final UUID userId, final UUID botId) {
        super(ChannelType.BOT);
        this.userId = userId;
        this.botId = botId;
    }

    public BotReferenceType to() {
        return new BotReferenceType(UserId.from(userId), BotId.from(botId));
    }

    public UUID userId() {
        return userId;
    }

    public UUID botId() {
        return botId;
    }
}

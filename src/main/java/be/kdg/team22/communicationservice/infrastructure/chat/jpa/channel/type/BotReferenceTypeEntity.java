package be.kdg.team22.communicationservice.infrastructure.chat.jpa.channel.type;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.BotReferenceType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
@DiscriminatorValue("BOT")
public class BotReferenceTypeEntity extends ReferenceTypeEntity {
    private UUID userId;

    protected BotReferenceTypeEntity() {}

    public BotReferenceTypeEntity(final UUID userId) {
        super(ChannelType.BOT);
        this.userId = userId;
    }

    public BotReferenceType to() {
        return new BotReferenceType(UserId.from(userId));
    }

    public UUID userId() {
        return userId;
    }
}

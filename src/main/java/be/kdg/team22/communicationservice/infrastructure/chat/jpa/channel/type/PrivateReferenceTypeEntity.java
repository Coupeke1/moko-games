package be.kdg.team22.communicationservice.infrastructure.chat.jpa.channel.type;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.PrivateReferenceType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
@DiscriminatorValue("FRIENDS")
public class PrivateReferenceTypeEntity extends ReferenceTypeEntity {
    @Column
    private UUID userId;
    @Column
    private UUID otherUserId;

    protected PrivateReferenceTypeEntity() {}

    public PrivateReferenceTypeEntity(final UUID userId, final UUID otherUserId) {
        super(ChannelType.FRIENDS);
        this.userId = userId;
        this.otherUserId = otherUserId;
    }

    public PrivateReferenceType to() {
        return new PrivateReferenceType(UserId.from(userId), UserId.from(otherUserId));
    }

    public UUID userId() {
        return userId;
    }

    public UUID otherUserId() {
        return otherUserId;
    }
}

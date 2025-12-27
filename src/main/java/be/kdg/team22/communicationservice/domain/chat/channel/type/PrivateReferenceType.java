package be.kdg.team22.communicationservice.domain.chat.channel.type;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;

public class PrivateReferenceType extends ReferenceType {
    private UserId userId;
    private UserId otherUserId;

    public PrivateReferenceType(final UserId userId, final UserId otherUserId) {
        super(ChannelType.FRIENDS);
        this.userId = userId;
        this.otherUserId = otherUserId;
    }

    public UserId userId() {
        return userId;
    }

    public UserId otherUserId() {
        return otherUserId;
    }
}

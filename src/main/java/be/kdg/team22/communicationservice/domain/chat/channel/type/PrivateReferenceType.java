package be.kdg.team22.communicationservice.domain.chat.channel.type;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;

public class PrivateReferenceType extends ReferenceType {
    private final UserId userId;
    private final UserId otherUserId;

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

    public UserId getOtherUser(final UserId id) {
        if (id.equals(userId)) return otherUserId;
        else if (id.equals(otherUserId))
            return userId;

        throw id.notFound();
    }

    public boolean hasUser(final UserId id) {
        return id.equals(userId) || id.equals(otherUserId);
    }
}

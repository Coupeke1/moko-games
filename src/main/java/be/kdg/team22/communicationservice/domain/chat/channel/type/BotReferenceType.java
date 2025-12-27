package be.kdg.team22.communicationservice.domain.chat.channel.type;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;

public class BotReferenceType extends ReferenceType {
    private UserId userId;

    public BotReferenceType(final UserId userId) {
        super(ChannelType.BOT);
        this.userId = userId;
    }

    public UserId userId() {
        return userId;
    }
}

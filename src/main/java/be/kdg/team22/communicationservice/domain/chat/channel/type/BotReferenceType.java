package be.kdg.team22.communicationservice.domain.chat.channel.type;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.bot.BotId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;

public class BotReferenceType extends ReferenceType {
    private final UserId userId;
    private final BotId botId;

    public BotReferenceType(final UserId userId, final BotId botId) {
        super(ChannelType.BOT);
        this.userId = userId;
        this.botId = botId;
    }

    public UserId userId() {
        return userId;
    }

    public BotId botId() {
        return botId;
    }
}

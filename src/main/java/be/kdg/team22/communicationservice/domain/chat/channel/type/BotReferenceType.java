package be.kdg.team22.communicationservice.domain.chat.channel.type;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.bot.BotId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;

public class BotReferenceType extends ReferenceType {
    private final UserId userId;
    private final BotId botId;
    private final String game;

    public BotReferenceType(final UserId userId, final BotId botId, final String game) {
        super(ChannelType.BOT);
        this.userId = userId;
        this.botId = botId;
        this.game = game;
    }

    public UserId userId() {
        return userId;
    }

    public BotId botId() {
        return botId;
    }

    public String game() {
        return game;
    }
}

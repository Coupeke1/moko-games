package be.kdg.team22.communicationservice.domain.chat.channel.type;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.bot.BotId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;
import be.kdg.team22.communicationservice.domain.chat.channel.GameId;

public class BotReferenceType extends ReferenceType {
    private final UserId userId;
    private final BotId botId;
    private final GameId gameId;

    public BotReferenceType(final UserId userId, final BotId botId, final GameId gameId) {
        super(ChannelType.BOT);
        this.userId = userId;
        this.botId = botId;
        this.gameId = gameId;
    }

    public UserId userId() {
        return userId;
    }

    public BotId botId() {
        return botId;
    }

    public GameId gameId() {
        return gameId;
    }
}

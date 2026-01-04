package be.kdg.team22.communicationservice.domain.chat.bot;

import be.kdg.team22.communicationservice.domain.chat.UserId;

public interface BotChatRepository {
    BotResponse ask(final UserId userId, final String question, final String gameName);
}
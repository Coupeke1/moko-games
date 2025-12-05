package be.kdg.team22.communicationservice.infrastructure.chat.bot;

import be.kdg.team22.communicationservice.domain.chat.bot.BotResponse;
import be.kdg.team22.communicationservice.domain.chat.bot.BotChatRepository;
import org.springframework.stereotype.Repository;

@Repository
public class DbBotChatRepository implements BotChatRepository {
    @Override
    public BotResponse askBot(String model, String userId, String reference, String message) {
        return null;
    }
}